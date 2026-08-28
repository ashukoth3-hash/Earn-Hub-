package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
import com.example.data.model.UserStats
import com.example.data.model.WithdrawalRecord
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldOrange
import com.example.ui.theme.GoldYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RedeemOption(
    val method: String,
    val title: String,
    val amountFormatted: String,
    val coinsRequired: Long,
    val icon: ImageVector,
    val gradient: List<Color>,
    val placeholderAccount: String
)

@Composable
fun WalletScreen(
    userStats: UserStats?,
    transactions: List<TransactionRecord>,
    withdrawals: List<WithdrawalRecord>,
    onRequestWithdrawal: (method: String, coins: Long, amount: String, destination: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coins = userStats?.coins ?: 300L
    val totalEarned = userStats?.totalEarned ?: 300L
    val totalWithdrawn = userStats?.totalWithdrawn ?: 0L

    var activeRedeemOption by remember { mutableStateOf<RedeemOption?>(null) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val redeemOptions = listOf(
        RedeemOption("UPI / PayTM", "Instant UPI Transfer", "₹50", 600, Icons.Default.AccountBalance, listOf(Color(0xFF0284C7), Color(0xFF0369A1)), "Enter UPI ID (e.g. mobile@upi)"),
        RedeemOption("UPI / PayTM", "Instant UPI Transfer", "₹100", 1200, Icons.Default.AccountBalance, listOf(Color(0xFF0284C7), Color(0xFF0369A1)), "Enter UPI ID (e.g. mobile@upi)"),
        RedeemOption("UPI / PayTM", "Instant UPI Transfer", "₹250", 3000, Icons.Default.AccountBalance, listOf(Color(0xFF0284C7), Color(0xFF0369A1)), "Enter UPI ID (e.g. mobile@upi)"),
        RedeemOption("PayPal", "PayPal USD Cash", "$5.00", 5000, Icons.Default.Payment, listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)), "Enter PayPal Email Address"),
        RedeemOption("PayPal", "PayPal USD Cash", "$10.00", 10000, Icons.Default.Payment, listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)), "Enter PayPal Email Address"),
        RedeemOption("Google Play", "Google Play Voucher", "₹100", 1200, Icons.Default.Redeem, listOf(Color(0xFF059669), Color(0xFF047857)), "Enter Email for digital code delivery"),
        RedeemOption("Amazon", "Amazon Gift Voucher", "₹250", 3000, Icons.Default.ShoppingBag, listOf(Color(0xFFD97706), Color(0xFFB45309)), "Enter Email for gift card voucher"),
        RedeemOption("Crypto USDT", "USDT (TRC20)", "$5.00", 5000, Icons.Default.CurrencyBitcoin, listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)), "Enter TRC20 Wallet Address")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Wallet Balance Overview
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF231346)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF381A6A), Color(0xFF210E45))))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "WALLET BALANCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFFC4B5FD)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = GoldAccent,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "%,d Coins".format(coins),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Total Earned", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(text = "+%,d".format(totalEarned), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                            Column {
                                Text(text = "Total Redeemed", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(text = "-%,d".format(totalWithdrawn), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
                            }
                            Column {
                                Text(text = "Conversion Rate", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(text = "1000 = $1", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
                            }
                        }
                    }
                }
            }
        }

        // Payout Options Heading
        item {
            Text(
                text = "Redeem Rewards & Cashout",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Redeem Cards List
        items(redeemOptions) { option ->
            RedeemCardItem(
                option = option,
                userCoins = coins,
                onClick = { activeRedeemOption = option }
            )
        }

        // Transaction History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = GoldYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Coin Ledger & History",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf("ALL", "SPINS", "GAMES", "WATCH", "TASKS", "WITHDRAWAL")
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = Color(0xFF2C1B00),
                            containerColor = Color(0xFF1E1139),
                            labelColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            }
        }

        // Filtered Transactions
        val filteredTransactions = transactions.filter { tx ->
            when (selectedFilter) {
                "SPINS" -> tx.type == TransactionType.SPIN_WHEEL
                "GAMES" -> tx.type in listOf(TransactionType.MATH_GAME, TransactionType.LUCKY_NUMBER_GAME, TransactionType.MEMORY_GAME, TransactionType.TAP_RUSH_GAME)
                "WATCH" -> tx.type == TransactionType.WATCH_VIDEO
                "TASKS" -> tx.type in listOf(TransactionType.TASK_COMPLETE, TransactionType.DAILY_CHECKIN, TransactionType.SCRATCH_CARD)
                "WITHDRAWAL" -> tx.type == TransactionType.WITHDRAWAL
                else -> true
            }
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0E33)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No transactions found in this category",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions) { tx ->
                TransactionRowItem(tx = tx)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Withdrawal Confirmation Dialog
    if (activeRedeemOption != null) {
        WithdrawalDialog(
            option = activeRedeemOption!!,
            userCoins = coins,
            onConfirm = { destination ->
                onRequestWithdrawal(
                    activeRedeemOption!!.method,
                    activeRedeemOption!!.coinsRequired,
                    activeRedeemOption!!.amountFormatted,
                    destination
                )
                activeRedeemOption = null
            },
            onDismiss = { activeRedeemOption = null }
        )
    }
}

@Composable
fun RedeemCardItem(
    option: RedeemOption,
    userCoins: Long,
    onClick: () -> Unit
) {
    val canRedeem = userCoins >= option.coinsRequired

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0E35)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("redeem_option_${option.method}_${option.amountFormatted}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(option.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${option.method} - ${option.amountFormatted}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%,d Coins required".format(option.coinsRequired),
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canRedeem) EmeraldGreen else Color(0xFF332050)
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = if (canRedeem) "Redeem" else "Lock",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canRedeem) Color.White else Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
fun TransactionRowItem(tx: TransactionRecord) {
    val isPositive = tx.coins >= 0
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(tx.timestamp))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160B2C)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isPositive) "+%,d Coins".format(tx.coins) else "-%,d Coins".format(-tx.coins),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isPositive) EmeraldGreen else CoralRed
                )
                if (tx.status != "COMPLETED") {
                    Text(
                        text = tx.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawalDialog(
    option: RedeemOption,
    userCoins: Long,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var accountInput by remember { mutableStateOf("") }
    val canRedeem = userCoins >= option.coinsRequired

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140826)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Redeem ${option.amountFormatted}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Method: ${option.method} (${option.coinsRequired} Coins)",
                    fontSize = 13.sp,
                    color = Color(0xFFC4B5FD)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = accountInput,
                    onValueChange = { accountInput = it },
                    label = { Text("Payout Details") },
                    placeholder = { Text(option.placeholderAccount, color = Color(0xFF64748B)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color(0xFF4C2777)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payout_account_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "• Transfers are processed directly to your account within 1-2 business hours.\n• Please double check your account ID before submitting.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onConfirm(accountInput) },
                    enabled = canRedeem && accountInput.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        disabledContainerColor = Color(0xFF2E194B)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_withdraw_btn")
                ) {
                    Text(
                        text = if (!canRedeem) "Insufficient Coins (Need ${option.coinsRequired})" else "Confirm & Cashout",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canRedeem && accountInput.isNotBlank()) Color.White else Color(0xFF9E9E9E)
                    )
                }
            }
        }
    }
}
