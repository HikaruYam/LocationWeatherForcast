package com.example.locationweatherforcast.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

class DragDropState(
    val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggedDistance by mutableFloatStateOf(0f)
        private set
    var draggedIndex by mutableIntStateOf(-1)
        private set
    var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
        private set

    private val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let { Pair(it.offset, it.offsetEnd) }

    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let { lazyListState.getVisibleItemInfoFor(absolute = it) }
            ?.let { item ->
                (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
            }

    private val currentIndexOfDraggedItem: Int?
        get() = initiallyDraggedElement?.let { it.index }

    private val overscrollJob = mutableStateOf<Boolean>(false)

    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also {
                draggedIndex = it.index
                initiallyDraggedElement = it
            }
    }

    fun onDragInterrupted() {
        draggedDistance = 0f
        draggedIndex = -1
        initiallyDraggedElement = null
        overscrollJob.value = false
    }

    fun onDrag(offset: Offset) {
        draggedDistance += offset.y

        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            val hoveringItem = lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { item ->
                    endOffset.toInt() > item.offset && startOffset.toInt() < item.offsetEnd && draggedIndex != item.index
                }

            hoveringItem?.let { item ->
                draggedIndex.let { draggedIdx ->
                    if (draggedIdx != -1) {
                        onMove.invoke(draggedIdx, item.index)
                        draggedIndex = item.index
                    }
                }
            }
        }
    }

    fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offsetEnd + draggedDistance
            return@let when {
                draggedDistance > 0 && endOffset > lazyListState.layoutInfo.viewportEndOffset -> {
                    (endOffset - lazyListState.layoutInfo.viewportEndOffset).toFloat()
                }
                draggedDistance < 0 && startOffset < lazyListState.layoutInfo.viewportStartOffset -> {
                    (startOffset - lazyListState.layoutInfo.viewportStartOffset).toFloat()
                }
                else -> 0f
            }
        } ?: 0f
    }
}

fun LazyListState.getVisibleItemInfoFor(absolute: Int): LazyListItemInfo? {
    return this
        .layoutInfo
        .visibleItemsInfo
        .getOrNull(absolute - this.layoutInfo.visibleItemsInfo.first().index)
}

val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState = rememberLazyListState(),
    onMove: (Int, Int) -> Unit
): DragDropState {
    return remember { DragDropState(lazyListState = lazyListState, onMove = onMove) }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.dragContainer(dragDropState: DragDropState): Modifier {
    return pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                dragDropState.onDragStart(offset)
            },
            onDragEnd = {
                dragDropState.onDragInterrupted()
            },
            onDrag = { change, dragAmount ->
                dragDropState.onDrag(Offset(dragAmount.x, dragAmount.y))
            }
        )
    }
}

@Composable
fun Modifier.draggedItem(
    dragDropState: DragDropState,
    index: Int
): Modifier {
    val isDragging = index == dragDropState.draggedIndex
    val zIndex = if (isDragging) 1f else 0f
    val elevation = if (isDragging) 8f else 0f
    
    return this
        .zIndex(zIndex)
        .graphicsLayer {
            shadowElevation = elevation
        }
        .then(
            if (isDragging) {
                Modifier.offset {
                    IntOffset(
                        0,
                        dragDropState.elementDisplacement?.roundToInt() ?: 0
                    )
                }
            } else Modifier
        )
}