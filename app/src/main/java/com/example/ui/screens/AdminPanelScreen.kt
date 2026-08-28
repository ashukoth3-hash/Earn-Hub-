package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserStats
import com.example.data.model.WithdrawalRecord
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.SkyBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanelScreen(
    userStats: UserStats?,
    withdrawals: List<WithdrawalRecord>,
    onApproveWithdrawal: (Long) -> Unit,
    onRejectWithdrawal: (Long, String) -> Unit,
    onMarkUnderReview: (Long) -> Unit,
    onAdjustCoins: (Long, String) -> Unit,
    onResetLimits: (Int, Int) -> Unit,
    onUpdateReferralCode: (String) -> Unit,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSection by remember { mutableIntStateOf(0) }
    val sectionTitles = listOf("Withdrawal Approvals", "User & Coins", "App Controls", "System Stats")

    var rejectDialogTarget by remember { mutableStateOf<WithdrawalRecord?>(null) }
    var adjustCoinsDialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F071A))
    ) {
        // Admin Top Bar
        Surface(
            color = Color(0xFF1E0C38),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onExitAdmin,
                        modifier = Modifier.testTag("admin_exit_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Exit Admin",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Admin Control Center",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Full Payout & System Control",
                            fontSize = 11.sp,
                            color = Color(0xFFC4B5FD)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF104A33)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ADMIN ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                }
            }
        }

        // Section Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            containerColor = Color(0xFF16092C),
            contentColor = GoldYellow,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                    color = GoldAccent,
                    height = 3.dp
                )
            }
        ) {
            sectionTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSection == index,
                    onClick = { selectedSection = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSection == index) GoldAccent else Color(0xFF94A3B8)
                        )
                    }
                )
            }
        }

        // Main Content View based on Selected Tab
        when (selectedSection) {
            0 -> {
                AdminWithdrawalsTab(
                    withdrawals = withdrawals,
                    onApprove = onApproveWithdrawal,
                    onRejectClick = { rejectDialogTarget = it },
                    onReview = onMarkUnderReview
                )
            }
            1 -> {
                AdminUserControlsTab(
                    userStats = userStats,
                    onOpenAdjustCoins = { adjustCoinsDialogVisible = true },
                    onResetLimits = onResetLimits,
                    onUpdateReferralCode = onUpdateReferralCode
                )
            }
            2 -> {
                AdminAppControlsTab(
                    userStats = userStats,
                    onResetLimits = onResetLimits,
                    onAdjustCoins = onAdjustCoins
                )
            }
            3 -> {
                AdminSystemStatsTab(
                    userStats = userStats,
                    withdrawals = withdrawals
                )
            }
        }
    }

    // Rejection Reason Dialog
    if (rejectDialogTarget != null) {
        RejectWithdrawalDialog(
            withdrawal = rejectDialogTarget!!,
            onConfirmReject = { reason ->
                onRejectWithdrawal(rejectDialogTarget!!.id, reason)
                rejectDialogTarget = null
            },
            onDismiss = { rejectDialogTarget = null }
        )
    }

    // Adjust Coins Dialog
    if (adjustCoinsDialogVisible) {
        AdjustCoinsDialog(
            currentCoins = userStats?.coins ?: 0L,
            onConfirm = { amount, reason ->
                onAdjustCoins(amount, reason)
                adjustCoinsDialogVisible = false
            },
            onDismiss = { adjustCoinsDialogVisible = false }
        )
    }
}

// -------------------------------------------------------------------------
// SECTION 1: WITHDRAWAL APPROVALS TAB
// -------------------------------------------------------------------------
@Composable
fun AdminWithdrawalsTab(
    withdrawals: List<WithdrawalRecord>,
    onApprove: (Long) -> Unit,
    onRejectClick: (WithdrawalRecord) -> Unit,
    onReview: (Long) -> Unit
) {
    var statusFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "PENDING", "APPROVED", "REJECTED", "UNDER_REVIEW")

    val pendingCount = withdrawals.count { it.status == "PENDING" }
    val approvedCount = withdrawals.count { it.status == "APPROVED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Summary Counter Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF281146)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Pending Approvals", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$pendingCount Requests",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldYellow
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF104A33)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Approved & Paid", fontSize = 11.sp, color = Color(0xFFB2DFDB))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$approvedCount Completed",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    FilterChip(
                        selected = statusFilter == filter,
                        onClick = { statusFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = Color(0xFF1F103A),
                            containerColor = Color(0xFF210E3F),
                            labelColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            }
        }

        val filteredWithdrawals = withdrawals.filter { w ->
            if (statusFilter == "ALL") true else w.status == statusFilter
        }

        if (filteredWithdrawals.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0B2E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No withdrawal requests in '$statusFilter' queue",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        } else {
            items(filteredWithdrawals) { item ->
                AdminWithdrawalRequestCard(
                    withdrawal = item,
                    onApprove = { onApprove(item.id) },
                    onReject = { onRejectClick(item) },
                    onReview = { onReview(item.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AdminWithdrawalRequestCard(
    withdrawal: WithdrawalRecord,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onReview: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(withdrawal.timestamp))
    val statusColor = when (withdrawal.status) {
        "APPROVED" -> EmeraldGreen
        "REJECTED" -> CoralRed
        "UNDER_REVIEW" -> GoldYellow
        else -> Color(0xFF60A5FA) // PENDING
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D0C38)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_withdrawal_item_${withdrawal.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF3B1A6E)
                    ) {
                        Text(
                            text = "#ID-${withdrawal.id}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = withdrawal.method,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                ) {
                    Text(
                        text = withdrawal.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount & Coins Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Requested Amount", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Text(
                        text = withdrawal.amountFormatted,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldYellow
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Coins Equivalent", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Text(
                        text = "%,d Coins".format(withdrawal.coinsDeducted),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Destination Account Details Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF28114C),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "PAYOUT DESTINATION ACCOUNT / ID:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC4B5FD)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = withdrawal.destinationAccount,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Requested at: $dateStr",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct Action Buttons for Admin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Approve Button
                Button(
                    onClick = onApprove,
                    enabled = withdrawal.status != "APPROVED",
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        disabledContainerColor = Color(0xFF1A3828)
                    ),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(42.dp)
                        .testTag("admin_approve_btn_${withdrawal.id}")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (withdrawal.status == "APPROVED") "Approved" else "Approve & Pay",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Reject & Refund Button
                Button(
                    onClick = onReject,
                    enabled = withdrawal.status != "REJECTED",
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CoralRed,
                        disabledContainerColor = Color(0xFF38151D)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("admin_reject_btn_${withdrawal.id}")
                ) {
                    Icon(imageVector = Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (withdrawal.status == "REJECTED") "Rejected" else "Reject",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Review Button
                OutlinedButton(
                    onClick = onReview,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(42.dp)
                ) {
                    Text(text = "Hold", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// SECTION 2: USER & COIN CONTROLS TAB
// -------------------------------------------------------------------------
@Composable
fun AdminUserControlsTab(
    userStats: UserStats?,
    onOpenAdjustCoins: () -> Unit,
    onResetLimits: (Int, Int) -> Unit,
    onUpdateReferralCode: (String) -> Unit
) {
    var newRefCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0D3B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Current User Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "User Coins Balance", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "%,d".format(userStats?.coins ?: 0L),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldYellow
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Lifetime Earnings", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "%,d".format(userStats?.totalEarned ?: 0L),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Spins Left: ${userStats?.dailySpinsLeft ?: 0}", color = Color.White, fontSize = 13.sp)
                        Text(text = "Scratch Cards Left: ${userStats?.scratchCardsLeft ?: 0}", color = Color.White, fontSize = 13.sp)
                        Text(text = "Streak: ${userStats?.streakDays ?: 1} Days", color = GoldYellow, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onOpenAdjustCoins,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_open_adjust_coins_btn")
                    ) {
                        Icon(imageVector = Icons.Default.CurrencyExchange, contentDescription = null, tint = Color(0xFF2C1B00))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add / Deduct User Coins Directly",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C1B00),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Change User Referral Code
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0D3B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Manage Referral Code",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Current Code: ${userStats?.referralCode ?: "CASH892"}",
                        fontSize = 13.sp,
                        color = GoldYellow,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newRefCode,
                            onValueChange = { newRefCode = it.uppercase() },
                            placeholder = { Text("e.g. VIP2026", color = Color(0xFF64748B)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color(0xFF4C2777)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newRefCode.isNotBlank()) {
                                    onUpdateReferralCode(newRefCode)
                                    newRefCode = ""
                                    Toast.makeText(context, "Referral Code updated!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("Update", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // One-Click Limit Resets
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0D3B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Reset User Limits & Tasks",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Instantly restore 10 spins, 5 scratch cards, and reset daily tasks.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onResetLimits(10, 5) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset Daily Limits Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// SECTION 3: APP & MONETIZATION CONTROLS TAB
// -------------------------------------------------------------------------
@Composable
fun AdminAppControlsTab(
    userStats: UserStats?,
    onResetLimits: (Int, Int) -> Unit,
    onAdjustCoins: (Long, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C0D37)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Active AdMob Monetization Configuration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• AdMob App ID: ca-app-pub-1601992247643052~1733291557\n• Watch & Earn Rewarded Unit: ca-app-pub-1601992247643052/8378590953\n• Game Over Interstitial Unit: ca-app-pub-1601992247643052/2384912724",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF104A33),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AdMob SDK Initialized & Ready",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C0D37)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Reward Multiplier Shortcuts",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onAdjustCoins(1000, "Admin Special Event Bonus") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B21A8)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+1000 Bonus", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onAdjustCoins(5000, "Admin VIP Jackpot") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9A3412)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+5000 Jackpot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// SECTION 4: SYSTEM STATS TAB
// -------------------------------------------------------------------------
@Composable
fun AdminSystemStatsTab(
    userStats: UserStats?,
    withdrawals: List<WithdrawalRecord>
) {
    val totalWithdrawnCoins = withdrawals.filter { it.status == "APPROVED" }.sumOf { it.coinsDeducted }
    val pendingCoins = withdrawals.filter { it.status == "PENDING" }.sumOf { it.coinsDeducted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "System Financial Ledger",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            AdminStatCard(
                title = "Total User Balance",
                value = "%,d Coins".format(userStats?.coins ?: 0L),
                icon = Icons.Default.MonetizationOn,
                color = GoldYellow
            )
        }

        item {
            AdminStatCard(
                title = "Total Approved Payouts",
                value = "%,d Coins Paid".format(totalWithdrawnCoins),
                icon = Icons.Default.CheckCircle,
                color = EmeraldGreen
            )
        }

        item {
            AdminStatCard(
                title = "Pending Withdrawal Liability",
                value = "%,d Coins Pending".format(pendingCoins),
                icon = Icons.Default.Payment,
                color = Color(0xFFFF8A80)
            )
        }

        item {
            AdminStatCard(
                title = "Total Invited Friends",
                value = "${userStats?.referralCount ?: 0} Users",
                icon = Icons.Default.People,
                color = SkyBlue
            )
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0C35)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontSize = 12.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}

// -------------------------------------------------------------------------
// REJECT DIALOG
// -------------------------------------------------------------------------
@Composable
fun RejectWithdrawalDialog(
    withdrawal: WithdrawalRecord,
    onConfirmReject: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var reasonInput by remember { mutableStateOf("Invalid payment ID / Details mismatch") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A26)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Reject & Refund Withdrawal #${withdrawal.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoralRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Amount: ${withdrawal.amountFormatted} (${withdrawal.coinsDeducted} Coins)\nDestination: ${withdrawal.destinationAccount}",
                    fontSize = 13.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text("Rejection Reason (Refund to User)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CoralRed,
                        unfocusedBorderColor = Color(0xFF552233)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = { onConfirmReject(reasonInput) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("Reject & Refund", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// ADJUST COINS DIALOG
// -------------------------------------------------------------------------
@Composable
fun AdjustCoinsDialog(
    currentCoins: Long,
    onConfirm: (Long, String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf("500") }
    var isAddition by remember { mutableStateOf(true) }
    var reasonText by remember { mutableStateOf("Admin Manual Adjustment") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF180A2E)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Adjust User Coins",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Current Balance: %,d Coins".format(currentCoins),
                    fontSize = 13.sp,
                    color = GoldYellow
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isAddition = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAddition) EmeraldGreen else Color(0xFF281146)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ ADD COINS", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { isAddition = false },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAddition) CoralRed else Color(0xFF281146)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("- DEDUCT COINS", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                    label = { Text("Coins Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color(0xFF4C2777)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Reason / Note") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color(0xFF4C2777)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val parsed = amountText.toLongOrNull() ?: 0L
                            val delta = if (isAddition) parsed else -parsed
                            onConfirm(delta, reasonText)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAddition) EmeraldGreen else CoralRed
                        ),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
