package com.justeat.jubako.recyclerviews

import androidx.lifecycle.LifecycleOwner
import com.justeat.jubako.ContentDescription
import com.justeat.jubako.ContentDescriptionCollection
import com.justeat.jubako.Jubako
import com.justeat.jubako.data.PaginatedDataState

interface ContentLoadingStrategy {
    /**
     * @param onLoaded A callback invoked when the strategy has loaded, clients should return true if they wish to load more
     */
    fun load(
        lifecycleOwner: LifecycleOwner,
        data: Jubako.Data,
        callback: (state: PaginatedDataState<ContentDescription<Any>>, hasMore: Boolean) -> Unit
    )

    fun reload(lifecycleOwner: LifecycleOwner, position: Int, descriptions: ContentDescriptionCollection)
    fun reset()
}
