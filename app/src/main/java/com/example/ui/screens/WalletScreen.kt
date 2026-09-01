package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
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

data class RedeemOption(
    val id: String,
    val method: String,
    val amountFormatted: String,
    val coinsRequired: Long,
    val icon: ImageVector,
    val gradient: List<Color>,
    val requiresBankDetails: Boolean = false,
    val placeholderAccount: String
) {
    val title: String get() = method
}

@Composable
fun WalletScreen(
    userStats: UserStats?,
    transactions: List<TransactionRecord>,
    withdrawals: List<WithdrawalRecord>,
    onRequestWithdrawal: (method: String, coins: Long, amount: String, destination: String, accountHolder: String, ifsc: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coins = userStats?.coins ?: 500L
    val gems = userStats?.gems ?: 0L
    val totalEarned = userStats?.totalEarned ?: 500L
    val totalWithdrawn = userStats?.totalWithdrawn ?: 0L

    var activeRedeemOption by remember { mutableStateOf<RedeemOption?>(null) }
    var selectedTab by remember { mutableStateOf("REDEEM") } // REDEEM, VOUCHERS, TRANSACTIONS

    val clipboardManager = LocalClipboardManager.current
    var copiedVoucherId by remember { mutableStateOf<Long?>(null) }

    val redeemOptions = listOf(
        // Google Play Redeem Code
        RedeemOption("gp_50", "Google Play Redeem Code", "₹50 Code", 600L, Icons.Default.Redeem, listOf(Color(0xFF0F9D58), Color(0xFF00796B)), false, "Google Account Email ID"),
        RedeemOption("gp_100", "Google Play Redeem Code", "₹100 Code", 1200L, Icons.Default.Redeem, listOf(Color(0xFF0F9D58), Color(0xFF00796B)), false, "Google Account Email ID"),
        RedeemOption("gp_250", "Google Play Redeem Code", "₹250 Code", 2900L, Icons.Default.Redeem, listOf(Color(0xFF0F9D58), Color(0xFF00796B)), false, "Google Account Email ID"),
        RedeemOption("gp_500", "Google Play Redeem Code", "₹500 Code", 5500L, Icons.Default.Redeem, listOf(Color(0xFF0F9D58), Color(0xFF00796B)), false, "Google Account Email ID"),

        // UPI Transfer
        RedeemOption("upi_50", "UPI (GPay/PhonePe/Paytm)", "₹50 Cash", 600L, Icons.Default.Payment, listOf(Color(0xFF0284C7), Color(0xFF0369A1)), false, "UPI ID (e.g. yourname@okaxis / number@paytm)"),
        RedeemOption("upi_100", "UPI (GPay/PhonePe/Paytm)", "₹100 Cash", 1200L, Icons.Default.Payment, listOf(Color(0xFF0284C7), Color(0xFF0369A1)), false, "UPI ID (e.g. yourname@okaxis / number@paytm)"),
        RedeemOption("upi_250", "UPI (GPay/PhonePe/Paytm)", "₹250 Cash", 2900L, Icons.Default.Payment, listOf(Color(0xFF0284C7), Color(0xFF0369A1)), false, "UPI ID (e.g. yourname@okaxis / number@paytm)"),
        RedeemOption("upi_500", "UPI (GPay/PhonePe/Paytm)", "₹500 Cash", 5500L, Icons.Default.Payment, listOf(Color(0xFF0284C7), Color(0xFF0369A1)), false, "UPI ID (e.g. yourname@okaxis / number@paytm)"),

        // Bank Transfer
        RedeemOption("bank_100", "Direct Bank Transfer (IMPS)", "₹100 Bank", 1200L, Icons.Default.AccountBalance, listOf(Color(0xFF4F46E5), Color(0xFF3730A3)), true, "Bank Account Number"),
        RedeemOption("bank_250", "Direct Bank Transfer (IMPS)", "₹250 Bank", 2900L, Icons.Default.AccountBalance, listOf(Color(0xFF4F46E5), Color(0xFF3730A3)), true, "Bank Account Number"),
        RedeemOption("bank_500", "Direct Bank Transfer (IMPS)", "₹500 Bank", 5500L, Icons.Default.AccountBalance, listOf(Color(0xFF4F46E5), Color(0xFF3730A3)), true, "Bank Account Number"),

        // Amazon Gift Card
        RedeemOption("amz_100", "Amazon Gift Card Voucher", "₹100 Gift Card", 1200L, Icons.Default.ShoppingBag, listOf(Color(0xFFFF9900), Color(0xFFCC7A00)), false, "Amazon Account Email ID / Mobile"),
        RedeemOption("amz_250", "Amazon Gift Card Voucher", "₹250 Gift Card", 2900L, Icons.Default.ShoppingBag, listOf(Color(0xFFFF9900), Color(0xFFCC7A00)), false, "Amazon Account Email ID / Mobile"),
        RedeemOption("amz_500", "Amazon Gift Card Voucher", "₹500 Gift Card", 5500L, Icons.Default.ShoppingBag, listOf(Color(0xFFFF9900), Color(0xFFCC7A00)), false, "Amazon Account Email ID / Mobile"),

        // Flipkart Gift Card
        RedeemOption("fk_100", "Flipkart Gift Card Voucher", "₹100 Voucher", 1200L, Icons.Default.ShoppingCart, listOf(Color(0xFF2874F0), Color(0xFF1B4FA0)), false, "Flipkart Account Email ID / Mobile"),
        RedeemOption("fk_250", "Flipkart Gift Card Voucher", "₹250 Voucher", 2900L, Icons.Default.ShoppingCart, listOf(Color(0xFF2874F0), Color(0xFF1B4FA0)), false, "Flipkart Account Email ID / Mobile")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Wallet Balance Header Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0E33)),
                border = BorderStroke(1.5.dp, GoldAccent),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF331359), Color(0xFF1B0A33))))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "WALLET & REDEEM HUB",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = GoldYellow
                            )

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GemCyan.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, GemCyan)
                            ) {
                                Text(
                                    text = "$gems 💎 Gems",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF67E8F9),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = GoldAccent,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "%,d Coins".format(coins),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Total Earned", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(text = "+%,d".format(totalEarned), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                            Column {
                                Text(text = "Total Withdrawn", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(text = "-%,d".format(totalWithdrawn), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
                            }
                            Column {
                                Text(text = "Payout Rate", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(text = "1200 = ₹100", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
                            }
                        }
                    }
                }
            }
        }

        // Section Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == "REDEEM",
                    onClick = { selectedTab = "REDEEM" },
                    label = { Text("🎁 Redeem Options") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAccent,
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("tab_redeem_options")
                )

                FilterChip(
                    selected = selectedTab == "VOUCHERS",
                    onClick = { selectedTab = "VOUCHERS" },
                    label = { Text("🔑 Vouchers & Payouts (${withdrawals.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldGreen,
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("tab_vouchers_history")
                )

                FilterChip(
                    selected = selectedTab == "TRANSACTIONS",
                    onClick = { selectedTab = "TRANSACTIONS" },
                    label = { Text("📜 History") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonPurple,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("tab_coin_history")
                )
            }
        }

        // 1. REDEEM OPTIONS LIST
        if (selectedTab == "REDEEM") {
            item {
                Text(
                    text = "Select Payment / Gift Card Method:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFE2E8F0)
                )
            }

            items(redeemOptions) { option ->
                val canRedeem = coins >= option.coinsRequired

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
                    border = BorderStroke(1.dp, if (canRedeem) GoldAccent.copy(alpha = 0.5f) else Color(0xFF2E1754)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeRedeemOption = option }
                        .testTag("redeem_option_${option.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(option.gradient)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.method,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = option.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = option.amountFormatted,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = GoldYellow
                                )
                            }
                        }

                        Button(
                            onClick = { activeRedeemOption = option },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canRedeem) GoldAccent else Color(0xFF2E1B56),
                                contentColor = if (canRedeem) Color.Black else Color(0xFF94A3B8)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "%,d Coins".format(option.coinsRequired),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. VOUCHERS & WITHDRAWALS STATUS TAB
        if (selectedTab == "VOUCHERS") {
            if (withdrawals.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎁", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Withdrawal Requests Yet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Pick a reward option above to submit your first redemption request!",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(withdrawals) { withdrawal ->
                    val isApproved = withdrawal.status == "APPROVED"
                    val isRejected = withdrawal.status == "REJECTED"
                    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                    val dateStr = dateFormat.format(Date(withdrawal.requestedAt))

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
                        border = BorderStroke(
                            1.dp,
                            if (isApproved) EmeraldGreen else if (isRejected) CoralRed else GoldAccent.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdrawal_card_${withdrawal.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = withdrawal.method,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = withdrawal.amountFormatted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = GoldYellow
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isApproved) Color(0xFF10B981).copy(alpha = 0.2f)
                                    else if (isRejected) Color(0xFFEF4444).copy(alpha = 0.2f)
                                    else Color(0xFFF59E0B).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (isApproved) "✓ APPROVED"
                                        else if (isRejected) "✕ REJECTED"
                                        else "⏳ UNDER REVIEW",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = if (isApproved) EmeraldGreen
                                        else if (isRejected) CoralRed
                                        else GoldYellow,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Account/ID: ${withdrawal.destination}",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                            if (withdrawal.accountHolderName.isNotBlank()) {
                                Text(
                                    text = "Name: ${withdrawal.accountHolderName}  |  IFSC: ${withdrawal.ifscCode}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(
                                text = "Requested: $dateStr • ${withdrawal.coinsDeducted} Coins",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            // 🔑 VOUCHER CODE BOX (Shown when approved with code)
                            if (isApproved && (!withdrawal.voucherCode.isNullOrBlank() || !withdrawal.utrReference.isNullOrBlank())) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF092E1F),
                                    border = BorderStroke(1.dp, EmeraldGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            if (!withdrawal.voucherCode.isNullOrBlank()) {
                                                Text(
                                                    text = "REDEEM VOUCHER CODE:",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = EmeraldGreen
                                                )
                                                Text(
                                                    text = withdrawal.voucherCode ?: "",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 15.sp,
                                                    color = Color.White
                                                )
                                            }
                                            if (!withdrawal.utrReference.isNullOrBlank()) {
                                                Text(
                                                    text = "UTR Ref: ${withdrawal.utrReference}",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF6EE7B7)
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                val toCopy = withdrawal.voucherCode ?: withdrawal.utrReference ?: ""
                                                clipboardManager.setText(AnnotatedString(toCopy))
                                                copiedVoucherId = withdrawal.id
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (copiedVoucherId == withdrawal.id) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (copiedVoucherId == withdrawal.id) "Copied!" else "Copy",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. TRANSACTIONS HISTORY TAB
        if (selectedTab == "TRANSACTIONS") {
            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "No coin transactions yet.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(transactions) { tx ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = tx.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(
                                text = if (tx.amount >= 0) "+%,d".format(tx.amount) else "%,d".format(tx.amount),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (tx.amount >= 0) EmeraldGreen else CoralRed
                            )
                        }
                    }
                }
            }
        }
    }

    // REDEMPTION INPUT DIALOG
    activeRedeemOption?.let { option ->
        WithdrawalRequestDialog(
            option = option,
            userCoins = coins,
            onConfirm = { dest, holder, ifsc ->
                onRequestWithdrawal(option.method, option.coinsRequired, option.amountFormatted, dest, holder, ifsc)
                activeRedeemOption = null
            },
            onDismiss = { activeRedeemOption = null }
        )
    }
}

@Composable
fun WithdrawalRequestDialog(
    option: RedeemOption,
    userCoins: Long,
    onConfirm: (destination: String, accountHolder: String, ifsc: String) -> Unit,
    onDismiss: () -> Unit
) {
    var destinationInput by remember { mutableStateOf("") }
    var accountHolderInput by remember { mutableStateOf("") }
    var ifscInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    val hasEnoughCoins = userCoins >= option.coinsRequired

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140A28)),
            border = BorderStroke(1.5.dp, GoldAccent),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("withdrawal_request_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = option.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${option.amountFormatted} • ${option.coinsRequired} Coins",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Account / ID input
                OutlinedTextField(
                    value = destinationInput,
                    onValueChange = {
                        destinationInput = it
                        inputError = null
                    },
                    label = { Text(option.placeholderAccount) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color(0xFF381F66),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdrawal_destination_input")
                )

                // Extra fields for Bank Transfer
                if (option.requiresBankDetails) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = accountHolderInput,
                        onValueChange = { accountHolderInput = it },
                        label = { Text("Account Holder Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF381F66),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = ifscInput,
                        onValueChange = { ifscInput = it },
                        label = { Text("Bank IFSC Code (e.g. SBIN0001234)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF381F66),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                inputError?.let { err ->
                    Text(
                        text = err,
                        color = CoralRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Policy Note
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E0F38),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔒 Requests are processed and approved by Admin. Redeem codes will be displayed instantly on your 'Vouchers' tab upon verification!",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (destinationInput.isBlank()) {
                            inputError = "Please enter your payment destination / email / UPI ID"
                            return@Button
                        }
                        if (!hasEnoughCoins) {
                            inputError = "Insufficient coins balance! Need ${option.coinsRequired - userCoins} more coins."
                            return@Button
                        }
                        onConfirm(destinationInput, accountHolderInput, ifscInput)
                    },
                    enabled = hasEnoughCoins,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        disabledContainerColor = Color(0xFF2E1A52)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_withdrawal_btn")
                ) {
                    Text(
                        text = if (hasEnoughCoins) "Submit Redemption Request" else "Insufficient Coins",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (hasEnoughCoins) Color.Black else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
