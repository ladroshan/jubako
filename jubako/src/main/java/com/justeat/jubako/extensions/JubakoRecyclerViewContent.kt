package com.justeat.jubako.extensions

import android.content.Context
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.justeat.jubako.*
import com.justeat.jubako.data.PaginatedLiveData
import com.justeat.jubako.data.PaginatedLiveData.State

/**
 * Convenience function to add a [RecyclerView] into the list to present data as carousels, grids, etc,
 * by default a [LinearLayoutManager] is configured in [HORIZONTAL] orientation.
 *
 * @param view  An inflated view for your holder must contain a [RecyclerView] as the root view
 * unless specified by [recyclerViewId]. This is not used if you are providing your own custom [JubakoRecyclerViewHolder]
 * with the [viewHolder] argument.
 * @param viewHolder Optionally provide your own custom [JubakoRecyclerViewHolder] implementation.
 * @param recyclerViewId The id of a [RecyclerView] in your layout, if omitted then [view] is expected
 * to be the recycler itself
 * @param viewBinder Optionally perform view binding in your [JubakoRecyclerViewHolder].
 * @param data The [DATA] that holds your list of things to display in your recycler view, use [InstantLiveData] for a simple
 * way to satisfy this argument if you already have something like a list of things you wish to display, otherwise
 * use your own [LiveData] if you want to load your [JubakoRecyclerViewHolder] with [DATA] from a service layer, etc.
 *
 * For paginated data use [PaginatedLiveData]
 * @param itemData Specify a lambda to retrieve the [ITEM_DATA] from your [DATA] by position.
 * @param itemCount Specify a lambda to retrieve the count of [ITEM_DATA] in your [DATA].
 * @param itemViewHolder Specify a custom [RecyclerView.ViewHolder] to use for your recycler view items.
 * @param itemBinder Optionally specify a lambda where you can bind [ITEM_DATA] to [ITEM_HOLDER]
 * @param layoutManager Defaults to a [LinearLayoutManager] in [HORIZONTAL] orientation, replace for a custom layout
 * @param onReload See [ContentDescription.onReload]
 *  * @param DATA The type of data you want to display, the data that contains all the items (see [ITEM_DATA]), simplest thing could be a [List]
 * @param HOLDER Optional custom type of [JubakoRecyclerViewHolder]
 * @param ITEM_DATA The type of item data that [DATA] provides
 * @param ITEM_HOLDER A standard implementation of [RecyclerView.ViewHolder] to display [ITEM_DATA]
 */
fun <DATA, HOLDER : JubakoRecyclerViewHolder<DATA, ITEM_DATA, ITEM_HOLDER>, ITEM_DATA, ITEM_HOLDER :
RecyclerView.ViewHolder> JubakoMutableList.addRecyclerView(
    view: ((parent: ViewGroup) -> View)? = null,
    viewHolder: (parent: ViewGroup) -> HOLDER = defaultRecyclerViewHolder(view),
    @IdRes recyclerViewId: Int = View.NO_ID,
    viewBinder: (holder: HOLDER) -> Unit = {},
    data: LiveData<DATA>,
    itemData: (data: DATA, position: Int) -> ITEM_DATA,
    itemCount: (data: DATA) -> Int,
    itemViewHolder: (parent: ViewGroup) -> ITEM_HOLDER,
    itemBinder: (holder: ITEM_HOLDER, data: ITEM_DATA?) -> Unit = { _, _ -> },
    layoutManager: (context: Context) -> RecyclerView.LayoutManager = { LinearLayoutManager(it, HORIZONTAL, false) },
    onReload: (ContentDescription<DATA>.(payload: Any?) -> Unit) = {}
) {
    add(descriptionProvider {
        recyclerViewContent(
            view,
            viewHolder,
            recyclerViewId,
            viewBinder,
            data,
            itemData,
            itemCount,
            itemViewHolder,
            itemBinder,
            layoutManager,
            onReload
        )
    })
}

/**
 * Convenience function to create a recycler view based content description
 *
 * @param view  An inflated view for your holder must contain a [RecyclerView] as the root view
 * unless specified by [recyclerViewId]. This is not used if you are providing your own custom [JubakoRecyclerViewHolder]
 * with the [viewHolder] argument.
 * @param viewHolder Optionally provide your own custom [JubakoRecyclerViewHolder] implementation.
 * @param recyclerViewId The id of a [RecyclerView] in your layout, if omitted then [view] is expected
 * to be the recycler itself
 * @param viewBinder Optionally perform view binding in your [JubakoRecyclerViewHolder].
 * @param data The [DATA] that holds your list of things to display in your recycler view, use [InstantLiveData] for a simple
 * way to satisfy this argument if you already have something like a list of things you wish to display, otherwise
 * use your own [LiveData] if you want to load your [JubakoRecyclerViewHolder] with [DATA] from a service layer, etc.
 *
 * For paginated data use [PaginatedLiveData]
 * @param itemData Specify a lambda to retrieve the [ITEM_DATA] from your [DATA] by position.
 * @param itemCount Specify a lambda to retrieve the count of [ITEM_DATA] in your [DATA].
 * @param itemViewHolder Specify a custom [RecyclerView.ViewHolder] to use for your recycler view items.
 * @param itemBinder Optionally specify a lambda where you can bind [ITEM_DATA] to [ITEM_HOLDER]
 * @param layoutManager Defaults to a [LinearLayoutManager] in [HORIZONTAL] orientation, replace for a custom layout
 * @param onReload See [ContentDescription.onReload]
 * @param DATA The type of data you want to display, the data that contains all the items (see [ITEM_DATA]), simplest thing could be a [List]
 * @param HOLDER Optional custom type of [JubakoRecyclerViewHolder]
 * @param ITEM_DATA The type of item data that [DATA] provides
 * @param ITEM_HOLDER A standard implementation of [RecyclerView.ViewHolder] to display [ITEM_DATA]
 */
fun <DATA, HOLDER : JubakoRecyclerViewHolder<DATA, ITEM_DATA, ITEM_HOLDER>, ITEM_DATA, ITEM_HOLDER : RecyclerView.ViewHolder> recyclerViewContent(
    view: ((parent: ViewGroup) -> View)? = null,
    viewHolder: (parent: ViewGroup) -> HOLDER = defaultRecyclerViewHolder(view),
    @IdRes recyclerViewId: Int = View.NO_ID,
    viewBinder: (holder: HOLDER) -> Unit = {},
    data: LiveData<DATA>,
    itemData: (data: DATA, position: Int) -> ITEM_DATA,
    itemCount: (data: DATA) -> Int,
    itemViewHolder: (parent: ViewGroup) -> ITEM_HOLDER,
    itemBinder: (holder: ITEM_HOLDER, data: ITEM_DATA?) -> Unit = { _, _ -> },
    layoutManager: (context: Context) -> RecyclerView.LayoutManager = { LinearLayoutManager(it, HORIZONTAL, false) },
    onReload: (ContentDescription<DATA>.(payload: Any?) -> Unit) = {}
): ContentDescription<DATA> {
    return ContentDescription(
        viewHolderFactory = viewHolderFactory { parent ->
            viewHolder(parent).also { holder ->
                holder.viewBinder = viewBinder as (Any) -> Unit
                holder.itemBinder = itemBinder
                holder.itemViewHolder = itemViewHolder
                holder.recyclerViewId = recyclerViewId
                holder.itemData = itemData
                holder.itemCount = itemCount
                holder.lifecycleOwner = (parent.context).takeIf { it is LifecycleOwner }?.let { it as LifecycleOwner }
                holder.layoutManager = layoutManager
                holder.paginatedLiveData = if (data is PaginatedLiveData<*>) data else null
            }
        },
        data = data,
        onReload = onReload
    )
}

internal fun <DATA, HOLDER : JubakoRecyclerViewHolder<DATA, ITEM_DATA, ITEM_HOLDER>, ITEM_DATA, ITEM_HOLDER : RecyclerView.ViewHolder> defaultRecyclerViewHolder(
    view: ((parent: ViewGroup) -> View)?
): (ViewGroup) -> HOLDER {
    return {
        JubakoRecyclerViewHolder<DATA, ITEM_DATA, ITEM_HOLDER>(
            view?.invoke(it) ?: throw Error("view is required with default viewHolder")
        ) as HOLDER
    }
}

/**
 * An implementation of [JubakoViewHolder] setup with a [RecyclerView] and easy callback functions for generic
 * view holder creation and data binding.
 *
 * Not normally used directly, use derived types with [addRecyclerView] or [recyclerViewContent] functions when
 * assembling content.
 */
open class JubakoRecyclerViewHolder<DATA, ITEM_DATA, ITEM_HOLDER : RecyclerView.ViewHolder>(itemView: View) :
    JubakoViewHolder<DATA>(itemView) {

    @IdRes
    internal var recyclerViewId: Int = View.NO_ID
    internal lateinit var itemViewHolder: (parent: ViewGroup) -> ITEM_HOLDER
    internal var viewBinder: (Any) -> Unit = {}
    internal var itemBinder: (holder: ITEM_HOLDER, data: ITEM_DATA?) -> Unit = { _, _ -> }
    internal var lifecycleOwner: LifecycleOwner? = null
    lateinit var itemData: (data: DATA, position: Int) -> ITEM_DATA
    lateinit var itemCount: (data: DATA) -> Int
    lateinit var layoutManager: (context: Context) -> RecyclerView.LayoutManager
    internal var paginatedLiveData: PaginatedLiveData<*>? = null

    private val recycler: RecyclerView by lazy {
        when (recyclerViewId) {
            View.NO_ID -> itemView as RecyclerView
            else -> itemView.findViewById(recyclerViewId)
        }.also { recycler ->
            val layoutManager = layoutManager.invoke(recycler.context)
            checkLayoutManager(layoutManager)
            recycler.layoutManager = layoutManager
        }
    }

    @CallSuper
    override fun bind(data: DATA?) {
        viewBinder(this)
        data?.apply {
            recycler.adapter = createAdapter(data)
            trackState()
        }
    }

    private fun trackState() {
        val cachedState: Parcelable? = (description?.cache?.get(RECYCLER_VIEW_STATE) as Parcelable?)
        cachedState?.let { inState ->
            recycler.layoutManager?.onRestoreInstanceState(inState)
        }

        lifecycleOwner?.lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                lifecycleOwner?.let {
                    paginatedLiveData?.removeObservers(it)
                    it.lifecycle.removeObserver(this)
                }
                recycler.layoutManager?.onSaveInstanceState()?.let { outState ->
                    description?.cache?.put(RECYCLER_VIEW_STATE, outState)
                }
            }
        })
    }

    private fun createAdapter(data: DATA) = Adapter(
        data = data,
        logger = logger,
        itemViewHolder = itemViewHolder,
        itemBinder = itemBinder,
        lifecycleOwner = lifecycleOwner,
        itemData = itemData,
        itemCount = itemCount,
        paginatedLiveData = paginatedLiveData
    )

    class Adapter<DATA, ITEM_DATA, ITEM_HOLDER : RecyclerView.ViewHolder>(
        private val data: DATA,
        var logger: Jubako.Logger,
        var itemViewHolder: (parent: ViewGroup) -> ITEM_HOLDER,
        var itemBinder: (holder: ITEM_HOLDER, data: ITEM_DATA?) -> Unit = { _, _ -> },
        var lifecycleOwner: LifecycleOwner? = null,
        var itemData: (data: DATA, position: Int) -> ITEM_DATA,
        var itemCount: (data: DATA) -> Int,
        var paginatedLiveData: PaginatedLiveData<*>? = null
    ) : RecyclerView.Adapter<ITEM_HOLDER>() {

        private var currentState: State<*>? = null

        var previousLastVisibleItemPos = Int.MIN_VALUE
        private var scrollListener: RecyclerView.OnScrollListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ITEM_HOLDER {
            logger.log(TAG, "Create View Holder", "")
            return itemViewHolder(parent)
        }

        override fun getItemCount(): Int = itemCount(currentState as? DATA?:data)
        override fun onBindViewHolder(holder: ITEM_HOLDER, position: Int) {
            logger.log(TAG, "Bind View Holder", "position: $position")
            itemBinder(holder, itemData(currentState as? DATA?:data, position))
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            setupPaginatedLoading(recyclerView)
        }

        private fun setupPaginatedLoading(recyclerView: RecyclerView) {
            if (paginatedLiveData != null) {
                lifecycleOwner?.let {
                    paginatedLiveData?.observe(it, Observer<State<*>> { state ->
                        if (state!!.accept()) {
                            currentState = state
                            logger.log(TAG, "Carousel Page Loaded", "")
                            val start = state.loaded.size - state.page.size
                            logger.log(
                                TAG,
                                "Notify Item Range Inserted",
                                "start: $start, count: ${state.page.size}"
                            )
                            notifyItemRangeInserted(start, state.page.size)
                            initialFillContinue(recyclerView)
                        }
                    })
                }
            }

            setupLoadMoreScrollTrigger(recyclerView)
            initialFillBegin(recyclerView)
        }

        private fun setupLoadMoreScrollTrigger(recyclerView: RecyclerView) {
            if (scrollListener != null) {
                recyclerView.removeOnScrollListener(scrollListener!!)
                scrollListener = null
            }

            scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    val offset = recyclerView.computeHorizontalScrollOffset()
                    val range =
                        recyclerView.computeHorizontalScrollRange() - recyclerView.computeHorizontalScrollExtent()
                    if (range != 0 && offset > range * 0.8f) {
                        logger.log(TAG, "Scroll Trigger Load", "")
                        paginatedLiveData?.loadMore()
                    }
                }
            }

            recyclerView.addOnScrollListener(scrollListener!!)
        }

        private fun initialFillBegin(recyclerView: RecyclerView) {
            logger.log(TAG, "Initial Fill Across", "begin...")
            paginatedLiveData?.loadMore()

            initialFillOnLayoutChanged(recyclerView)
        }

        private fun initialFillOnLayoutChanged(recyclerView: RecyclerView) {
            recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    recyclerView.removeOnLayoutChangeListener(this)
                    logger.log(TAG, "Initial Fill Across", "On Layout")

                    paginatedLiveData?.loadMore()
                }
            })
        }

        private fun initialFillContinue(recyclerView: RecyclerView) {

            if (previousLastVisibleItemPos == Int.MAX_VALUE) return // we are done here, no more to fill

            val lastVisibleItemPos =
                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            logger.log(
                TAG, "Initial Fill Across",
                "last visible item pos: $lastVisibleItemPos, previously: $previousLastVisibleItemPos"
            )

            when {
                canLoadMoreDescriptions(
                    paginatedLiveData?.hasMore ?: false,
                    lastVisibleItemPos,
                    previousLastVisibleItemPos
                ) -> {
                    logger.log(TAG, "Initial Fill Across", "fill more...")
                    previousLastVisibleItemPos = lastVisibleItemPos
                    paginatedLiveData?.loadMore()
                }
                else -> {
                    logger.log(TAG, "Initial Fill Across", "Complete")
                    previousLastVisibleItemPos = Int.MAX_VALUE
                }
            }
        }

        private fun canLoadMoreDescriptions(hasMore: Boolean, lastPos: Int, lastTimeVisibleItemPos: Int) =
            hasMore && lastPos != lastTimeVisibleItemPos
    }


    private fun checkLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        if (paginatedLiveData != null &&
            (layoutManager !is LinearLayoutManager ||
                    layoutManager.orientation != HORIZONTAL)
        ) {
            throw RuntimeException("You must use a LinearLayoutManager in HORIZONTAL orientation with PaginatedLiveData<T>")
        }
    }
}

private val TAG = JubakoRecyclerViewHolder::class.java.simpleName
private const val RECYCLER_VIEW_STATE = "jubako_recycler_view_state"