package com.example.bookinf.data

import kotlinx.coroutines.delay
import kotlin.String

interface DataProviderI {
    // первый шаг
    // val contentRooms: List<TradeOfferData>
    // val promoTags: Map<String, PromoData>

    // Следующий шаги
    // suspend означает, что функция может быть приостановлена
    // и не будет блокировать основной поток (UI)
    suspend fun getPromoTags(): Result<Map<String, PromoData>>
    suspend fun getContentRooms(): Result<List<TradeOfferData>>
}

object LocalDataProvider : DataProviderI {

    // get() = listOf(...) или get() = mapOf(...)
    // означает, что при каждом обращении к свойству в памяти
    // будет создаваться новый список.
    // Для заглушек лучше использовать просто
    // val contentRooms = listOf(...) или get() = mapOf(...) без кастомного геттера.

    // Первый шаг
    // override val promoTags: Map<String, PromoData> = mapOf(
    // override val contentRooms: List<TradeOfferData> = listOf(

    //
    override suspend fun getPromoTags(): Result<Map<String, PromoData>> {
        delay(1000) //для имитации сети
        // Имитируем успешный ответ
        return Result.success(
            mapOf(
                Pair("LimitedTime", PromoData("Limited-time Deal", true)),
                Pair("MobileOnly", PromoData("Mobile-only price")),
                Pair("EarlyDeal", PromoData("Early 2025 Deal")),
            )
        )
    }

    override suspend fun getContentRooms(): Result<List<TradeOfferData>> {
        delay(1000) // Для имитации сети
        // Имитируем успешный ответ
        return Result.success(
            listOf(
                TradeOfferData(
                    hotelName = "NH Barcelona Eixample",
                    score = 7.9f,
                    reviews = 2023,
                    location = "Eixample",
                    distanceFromCentre = 1300, // Удобно хранить в метрах (1.3 km)
                    promoTagKey = "Limited-time Deal",
                    beds = 2,
                    oldPrice = 281,
                    discountedPrice = 200,
                    currency = '€',
                    notPrepaymentNeeded = false,
                    remaining = 1,
                    includesTaxes = true,
                    certified = true,
                    isGeniusRecommended = true
                ),

                TradeOfferData(
                    hotelName = "Hotel Conqueridor",
                    score = 8.7f,
                    reviews = 4303,
                    location = "Extramurs",
                    distanceFromCentre = 450,
                    beds = 2,
                    oldPrice = 111,
                    discountedPrice = 99,
                    notPrepaymentNeeded = true,
                ),

                TradeOfferData(
                    hotelName = "Lindala",
                    score = 8.6f,
                    reviews = 1720,
                    location = "Poblats Mari",
                    distanceFromCentre = 450,
                    beds = 2,
                    oldPrice = 450,
                    discountedPrice = 390,
                    notPrepaymentNeeded = true,
                ),

                TradeOfferData(
                    hotelName = "Olivera Rooms",
                    score = 6.9f,
                    reviews = 2056,
                    location = "Elxample",
                    distanceFromCentre = 1400,
                    promoTagKey = "MobileOnly",
                    beds = 1,
                    oldPrice = 281,
                    discountedPrice = 100,
                    notPrepaymentNeeded = false,
                ),

                TradeOfferData(
                    hotelName = "YOU & CO",
                    score = 6.9f,
                    reviews = 2000,
                    location = "Elxample",
                    distanceFromCentre = 1100,
                    promoTagKey = "MobileOnly",
                    beds = 2,
                    oldPrice = 281,
                    discountedPrice = 70,
                    notPrepaymentNeeded = false,
                ),
            )
        )
    }
}
