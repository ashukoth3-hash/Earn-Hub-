package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Diamond
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
import com.example.ui.theme.GemCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SkyBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanelScreen(
    userStats: UserStats?,
    withdrawals: List<WithdrawalRecord>,
    onApproveWithdrawal: (Long, String?, String?, String?) -> Unit,
    onRejectWithdrawal: (Long, String) -> Unit,
    onMarkUnderReview: (Long) -> Unit,
    onAdjustCoins: (Long, String) -> Unit,
    onAdjustGems: (Long, String) -> Unit = { _, _ -> },
    onResetLimits: (Int, Int) -> Unit,
    onUpdateReferralCode: (String) -> Unit,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSection by remember { mutableIntStateOf(0) }
    val sectionTitles = listOf("Withdrawal Approvals", "User & Balances", "App Controls", "System Stats")

    var approveDialogTarget by remember { mutableStateOf<WithdrawalRecord?>(null) }
    var rejectDialogTarget by remember { mutableStateOf<WithdrawalRecord?>(null) }
    var adjustCoinsDialogVisible by remember { mutableStateOf(false) }
    var adjustGemsDialogVisible by remember { mutableStateOf(false) }

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
                            text = "Payouts & System Management",
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

        // Content Sections
        when (selectedSection) {
            0 -> AdminWithdrawalsSection(
                withdrawals = withdrawals,
                onApproveClick = { approveDialogTarget = it },
                onRejectClick = { rejectDialogTarget = it },
                onReview = {
                    onMarkUnderReview(it)
                    Toast.makeText(context, "Marked as Under Review", Toast.LENGTH_SHORT).show()
                }
            )
            1 -> AdminUserSection(
                userStats = userStats,
                onOpenAdjustCoins = { adjustCoinsDialogVisible = true },
                onOpenAdjustGems = { adjustGemsDialogVisible = true },
                onResetLimits = {
                    onResetLimits(10, 5)
                    Toast.makeText(context, "Daily limits reset to 10 Spins & 5 Cards", Toast.LENGTH_SHORT).show()
                }
            )
            2 -> AdminControlsSection(
                userStats = userStats,
                onUpdateReferral = { code ->
                    onUpdateReferralCode(code)
                    Toast.makeText(context, "Referral Code updated to: $code", Toast.LENGTH_SHORT).show()
                }
            )
            3 -> AdminStatsSection(
                userStats = userStats,
                withdrawals = withdrawals
            )
        }
    }

    // Approve Dialog with Voucher / UTR input
    approveDialogTarget?.let { record ->
        AdminApproveDialog(
            record = record,
            onConfirm = { voucher, utr, note ->
                onApproveWithdrawal(record.id, voucher, utr, note)
                approveDialogTarget = null
                Toast.makeText(context, "Withdrawal #${record.id} Approved & Voucher Created!", Toast.LENGTH_LONG).show()
            },
            onDismiss = { approveDialogTarget = null }
        )
    }

    // Reject Dialog
    rejectDialogTarget?.let { record ->
        AdminRejectDialog(
            record = record,
            onConfirm = { reason ->
                onRejectWithdrawal(record.id, reason)
                rejectDialogTarget = null
                Toast.makeText(context, "Withdrawal #${record.id} Rejected and Coins Refunded", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { rejectDialogTarget = null }
        )
    }

    // Adjust Coins Dialog
    if (adjustCoinsDialogVisible) {
        AdminAdjustCoinsDialog(
            currentCoins = userStats?.coins ?: 0L,
            onConfirm = { amount, reason ->
                onAdjustCoins(amount, reason)
                adjustCoinsDialogVisible = false
                Toast.makeText(context, "Coins balance adjusted by $amount", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { adjustCoinsDialogVisible = false }
        )
    }

    // Adjust Gems Dialog
    if (adjustGemsDialogVisible) {
        AdminAdjustGemsDialog(
            currentGems = userStats?.gems ?: 0L,
            onConfirm = { delta, reason ->
                onAdjustGems(delta, reason)
                adjustGemsDialogVisible = false
                Toast.makeText(context, "Gems adjusted by $delta", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { adjustGemsDialogVisible = false }
        )
    }
}

@Composable
fun AdminWithdrawalsSection(
    withdrawals: List<WithdrawalRecord>,
    onApproveClick: (WithdrawalRecord) -> Unit,
    onRejectClick: (WithdrawalRecord) -> Unit,
    onReview: (Long) -> Unit
) {
    var statusFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "PENDING", "UNDER_REVIEW", "APPROVED", "REJECTED")

    val pendingCount = withdrawals.count { it.status == "PENDING" }
    val approvedCount = withdrawals.count { it.status == "APPROVED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
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
                        Text(text = "Approved & Vouchers", fontSize = 11.sp, color = Color(0xFFB2DFDB))
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
                    onApprove = { onApproveClick(item) },
                    onReject = { onRejectClick(item) },
                    onReview = { onReview(item.id) }
                )
            }
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
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(withdrawal.requestedAt))
    val isApproved = withdrawal.status == "APPROVED"
    val isPending = withdrawal.status == "PENDING" || withdrawal.status == "UNDER_REVIEW"

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D0C38)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_withdrawal_item_${withdrawal.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ID: #${withdrawal.id} • ${withdrawal.method}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = withdrawal.amountFormatted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldYellow
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (withdrawal.status) {
                        "APPROVED" -> EmeraldGreen.copy(alpha = 0.2f)
                        "REJECTED" -> CoralRed.copy(alpha = 0.2f)
                        else -> GoldYellow.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = withdrawal.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (withdrawal.status) {
                            "APPROVED" -> EmeraldGreen
                            "REJECTED" -> CoralRed
                            else -> GoldYellow
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Destination: ${withdrawal.destination}",
                fontSize = 12.sp,
                color = Color(0xFFCBD5E1)
            )

            if (withdrawal.accountHolderName.isNotBlank()) {
                Text(
                    text = "Beneficiary: ${withdrawal.accountHolderName} | IFSC: ${withdrawal.ifscCode}",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Text(
                text = "Coins Deducted: ${withdrawal.coinsDeducted} • Date: $dateStr",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 2.dp)
            )

            if (isApproved && !withdrawal.voucherCode.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🔑 Voucher Code Issued: ${withdrawal.voucherCode}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreen
                )
            }

            if (isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_approve_btn_${withdrawal.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve & Code", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject", color = CoralRed, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminApproveDialog(
    record: WithdrawalRecord,
    onConfirm: (voucherCode: String?, utrRef: String?, note: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val isVoucherType = record.method.contains("Google Play", true) ||
            record.method.contains("Amazon", true) ||
            record.method.contains("Flipkart", true)

    val defaultCode = when {
        record.method.contains("Google Play", true) -> "GPAY-${(1000..9999).random()}-${(1000..9999).random()}-REDEEM"
        record.method.contains("Amazon", true) -> "AMZ-${(100000..999999).random()}-GIFT"
        record.method.contains("Flipkart", true) -> "FK-${(100000..999999).random()}-VOUCH"
        else -> ""
    }

    var voucherCode by remember { mutableStateOf(defaultCode) }
    var utrRef by remember { mutableStateOf(if (!isVoucherType) "UTR${(10000000..99999999).random()}" else "") }
    var adminNote by remember { mutableStateOf("Payment approved and verified by Admin.") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF160A2A)),
            border = BorderStroke(1.5.dp, EmeraldGreen),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Approve Withdrawal #${record.id}",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color.White
                )
                Text(
                    text = "${record.method} • ${record.amountFormatted} -> ${record.destination}",
                    fontSize = 12.sp,
                    color = GoldYellow
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (isVoucherType) {
                    OutlinedTextField(
                        value = voucherCode,
                        onValueChange = { voucherCode = it },
                        label = { Text("Redeem / Gift Card Voucher Code") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("admin_voucher_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    OutlinedTextField(
                        value = utrRef,
                        onValueChange = { utrRef = it },
                        label = { Text("Bank UTR / UPI Transaction Reference") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("admin_utr_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = adminNote,
                    onValueChange = { adminNote = it },
                    label = { Text("Admin Note for User") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onConfirm(
                            if (voucherCode.isNotBlank()) voucherCode else null,
                            if (utrRef.isNotBlank()) utrRef else null,
                            adminNote
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_confirm_approve_btn")
                ) {
                    Text("Confirm & Send to User", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun AdminRejectDialog(
    record: WithdrawalRecord,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var reason by remember { mutableStateOf("Invalid payment details / UPI ID not found. Coins refunded.") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C0A1F)),
            border = BorderStroke(1.dp, CoralRed),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Reject Request #${record.id}", fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Rejection") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { onConfirm(reason) },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reject & Refund Coins", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AdminAdjustCoinsDialog(
    currentCoins: Long,
    onConfirm: (Long, String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf("500") }
    var isAdd by remember { mutableStateOf(true) }
    var reason by remember { mutableStateOf("Admin Bonus") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF150A28)),
            border = BorderStroke(1.dp, GoldAccent),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Adjust User Coins", fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
                Text(text = "Current: $currentCoins Coins", color = GoldYellow, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isAdd = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isAdd) EmeraldGreen else Color(0xFF2E1754)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ Add Coins", color = if (isAdd) Color.Black else Color.White)
                    }
                    Button(
                        onClick = { isAdd = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isAdd) CoralRed else Color(0xFF2E1754)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("- Deduct", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Coin Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        val num = amountText.toLongOrNull() ?: 0L
                        val delta = if (isAdd) num else -num
                        onConfirm(delta, reason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Adjustment", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun AdminAdjustGemsDialog(
    currentGems: Long,
    onConfirm: (Long, String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf("10") }
    var isAdd by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, GemCyan),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Adjust User Gems 💎", fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
                Text(text = "Current: $currentGems Gems", color = GemCyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isAdd = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isAdd) GemCyan else Color(0xFF1E293B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ Add Gems", color = if (isAdd) Color.Black else Color.White)
                    }
                    Button(
                        onClick = { isAdd = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isAdd) CoralRed else Color(0xFF1E293B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("- Deduct", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Gems Count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        val num = amountText.toLongOrNull() ?: 0L
                        val delta = if (isAdd) num else -num
                        onConfirm(delta, "Admin Gem Adjustment")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GemCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Gems", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun AdminUserSection(
    userStats: UserStats?,
    onOpenAdjustCoins: () -> Unit,
    onOpenAdjustGems: () -> Unit,
    onResetLimits: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1D0C38)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "User Profile & Balances", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Username: ${userStats?.userName ?: "Earn Hub User"}", color = Color.White, fontSize = 13.sp)
                    Text(text = "Coins Balance: %,d Coins".format(userStats?.coins ?: 0L), color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Gems Vault: ${userStats?.gems ?: 0L} 💎", color = GemCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Mega 25 Gems Offer Progress: ${userStats?.megaOfferProgress ?: 0}/25 Steps", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Text(text = "Super 5K Offer Progress: ${userStats?.superOfferProgress ?: 0}/25 Steps", color = Color(0xFFCBD5E1), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onOpenAdjustCoins,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Adjust Coins", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onOpenAdjustGems,
                            colors = ButtonDefaults.buttonColors(containerColor = GemCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Adjust Gems 💎", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onResetLimits,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset Daily Spins & Scratch Limits", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminControlsSection(
    userStats: UserStats?,
    onUpdateReferral: (String) -> Unit
) {
    var newRefCode by remember { mutableStateOf(userStats?.referralCode ?: "EARNHUB100") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1D0C38)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "App & Referral Controls", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newRefCode,
                        onValueChange = { newRefCode = it.uppercase() },
                        label = { Text("Global Promo Referral Code") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onUpdateReferral(newRefCode) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Code", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatsSection(
    userStats: UserStats?,
    withdrawals: List<WithdrawalRecord>
) {
    val totalApproved = withdrawals.filter { it.status == "APPROVED" }.sumOf { it.coinsDeducted }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1D0C38)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Financial & App Analytics", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "• Total Coins Minted: %,d Coins".format(userStats?.totalEarned ?: 0L), color = Color(0xFFE2E8F0))
                    Text(text = "• Total Coins Redeemed: %,d Coins".format(totalApproved), color = GoldYellow)
                    Text(text = "• Current Circulation: %,d Coins".format(userStats?.coins ?: 0L), color = EmeraldGreen)
                    Text(text = "• Total Withdrawal Requests: ${withdrawals.size}", color = Color(0xFFE2E8F0))
                }
            }
        }
    }
}
