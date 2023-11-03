package com.gp.chat.presentation.groupchat

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.GroupMessage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random

object DummyData {
    val random = Random()
    val senderNames = listOf("Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace", "Hannah", "Ivy", "Jack")

    fun getRandomSenderName(): String {
        return senderNames[random.nextInt(senderNames.size)]
    }

    fun generateRandomTimestamp(): String {
        val hour = random.nextInt(24)
        val minute = random.nextInt(60)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return sdf.format(calendar.time)
    }

    val data = listOf(
        GroupMessage(
            id = "1",
            message = "Hello, everyone!",
            messageDate = Date(System.currentTimeMillis() - 86400000),
            senderId = Firebase.auth.currentUser?.email,
            senderName = "Mega Nigga",
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        GroupMessage(
            id = "2",
            message = "Good morning!",
            messageDate = Date(System.currentTimeMillis() - 86400000),
            senderId = "user2",
            senderName = getRandomSenderName(),
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        GroupMessage(
            id = "3",
            message = "How are you all doing?",
            messageDate = Date(System.currentTimeMillis() - 86400000),
            senderId = Firebase.auth.currentUser?.email,
            senderName = "Mega Nigga",
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        // Messages with the same messageDate
        GroupMessage(
            id = "4",
            message = "I'm doing great!",
            messageDate = Date(System.currentTimeMillis() - 86400000),
            senderId = "user4",
            senderName = getRandomSenderName(),
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        GroupMessage(
            id = "5",
            message = "Let's plan for the weekend.",
            messageDate = Date(System.currentTimeMillis() - 86400000),
            senderId = "user4",
            senderName = getRandomSenderName(),
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        // Messages with different messageDate
        GroupMessage(
            id = "6",
            message = "I'm in!",
            messageDate = Date(System.currentTimeMillis() - 172800000), // 1 day ago
            senderId = "user6",
            senderName = getRandomSenderName(),
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        GroupMessage(
            id = "7",
            message = "Count me in too.",
            messageDate = Date(System.currentTimeMillis() - 172800000), // 1 day ago
            senderId = Firebase.auth.currentUser?.email,
            senderName = "Mega Nigga",
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        GroupMessage(
            id = "8",
            message = "I'll join as well.",
            messageDate = Date(System.currentTimeMillis() - 172800000), // 1 day ago
            senderId = "user8",
            senderName = getRandomSenderName(),
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        GroupMessage(
            id = "9",
            message = "Let's meet at the park.",
            messageDate = Date(),
            senderId = "user9",
            senderName = getRandomSenderName(),
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        ),
        GroupMessage(
            id = "10",
            message = "Sounds like a plan!",
            messageDate = Date(),
            senderId = Firebase.auth.currentUser?.email,
            senderName = "Mega Nigga",
            senderPfpURL = "https://clipart-library.com/data_images/6103.png",
            timestamp = generateRandomTimestamp()
        )
    )
}