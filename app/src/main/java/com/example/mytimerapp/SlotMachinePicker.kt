package com.example.mytimerapp

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import android.util.Log

@Composable
fun SlotMachinePicker(
    modifier: Modifier = Modifier,
    range: IntRange = 0..59,
    initialValue: Int = 0,
    onNumberSelected: (Int) -> Unit
) {
    val itemHeight = 40.dp
    val visibleItems = 3
    val repeatCount = 3
    val numbers = List(repeatCount) { range.toList() }.flatten()

    var alignCenter = false
    
    val initialIndex = numbers.indexOf(initialValue) + (repeatCount / 2) * range.count() + 1
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex - (visibleItems / 2)
    )
    val coroutineScope = rememberCoroutineScope()
    
    var selectedNumber by remember { mutableStateOf(initialValue) }
    var lastStableNumber by remember { mutableStateOf(initialValue) }
    var selectionJob by remember { mutableStateOf<Job?>(null) }


    // 監聽滾動狀態
    LaunchedEffect(listState) {
        snapshotFlow { 
            listState.firstVisibleItemIndex to listState.isScrollInProgress 
        }.collect { (index, isScrolling) ->
            val centerIndex = index + (visibleItems / 2)
            val currentNumber = numbers[(centerIndex-0.5).toInt() % numbers.size]

            if (isScrolling) {
                // 取消之前的計時器
                selectionJob?.cancel()
                selectionJob = null
                if (!alignCenter) {
                    alignCenter = true
                    // 確保對齊到中心
                    coroutineScope.launch {
                        listState.animateScrollToItem(
                            index = centerIndex,
                        )
                        Log.d("SlotMachinePicker", "centerIndex: $centerIndex")
                    }
                }
            } else {
                // reset alignCenter
                alignCenter = false
                // 數字改變或停止滾動時
                if (currentNumber != lastStableNumber || selectionJob == null) {
                    lastStableNumber = currentNumber
                    selectedNumber = currentNumber
                    
                    // 取消之前的計時器並啟動新的
                    selectionJob?.cancel()
                    selectionJob = coroutineScope.launch {
                        // delay(5000) // 等待5秒
                        onNumberSelected(currentNumber)
                    }
                }
                 
            }
        }
    }

    // 處理循環滾動
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val totalItems = numbers.size
        val currentIndex = listState.firstVisibleItemIndex
        
        // 當滾動到接近邊界時，跳轉到中間的相同數字位置
        when {
            currentIndex < range.count() -> {
                listState.scrollToItem(currentIndex + range.count())
            }
            currentIndex >= totalItems - range.count() -> {
                listState.scrollToItem(currentIndex - range.count())
            }
        }
    }

    // 清理效果
    DisposableEffect(Unit) {
        onDispose {
            selectionJob?.cancel()
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .height(itemHeight * visibleItems)
                .width(80.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = itemHeight * ((visibleItems - 1) / 2)),
                userScrollEnabled = true
            ) {
                items(numbers.size) { index ->
                    val number = numbers[index % range.count()]
                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format("%02d", number),
                            fontSize = 24.sp,
                            fontWeight = if (number == selectedNumber) FontWeight.Bold else FontWeight.Normal,
                            color = if (number == selectedNumber) Color.Blue else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 中央框框
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = Color.Blue,
                        shape = MaterialTheme.shapes.medium
                    )
            )
        }
    }
}
