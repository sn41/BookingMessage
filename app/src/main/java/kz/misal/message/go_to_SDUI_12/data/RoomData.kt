package com.example.bookinf.data

import kotlin.math.roundToInt

// Как связать перечисление и локализованные ресурсы
//enum class OfferTag(
//    @StringRes val stringRes: Int,
//    @ColorRes val backgroundColor: Color,
//    @DrawableRes val iconRes: Int? = null,
//) {
//    MOBILE_ONLY_PRICE(R.string.mobile_only_price),
//    LIMITED_TIME(R.string.limited_time_deal),
//}

data class PromoData(
    val tag: String, val accented: Boolean = false
)


data class HotelData(
    val hotel: String,
    val location: String,
    val distanceFromCentre: Int,
    val certified: Boolean
) {
    val formattedDistance: String
        get() = if (distanceFromCentre >= 1000) {
            val distanceInKm = distanceFromCentre / 1000.0
            "%.1f".format(distanceInKm)

            // Этот вариант нехорош, он привязан только к одной локали
            // String.format(Locale.US, "%.1f km from centre", )

        } else {
            "$distanceFromCentre m from centre"
        }
}

data class OfferStateData(
    // оценка
    val score: Float,
    // просмотры
    val reviews: Int,
    // осталось на сайте
    val remaining: Int,
    // промо
    val promoTagKey: String,
) {
    val ratingText: String
        get() = when {
            score > 8 -> "Fabulous"
            score > 7 -> "Good"
            else -> "N/A"
        }

    val stars: Int
        get() = (score / 2).roundToInt()

    val hasRemainingCount: Boolean
        get() = remaining > 0

    val hasPromoTag: Boolean
        get() = promoTagKey.isNotEmpty()

}


data class PriceDetailData(
    val oldPrice: Int,
    val discountedPrice: Int = oldPrice,
    val currency: Char = '€',
    val nights: Int = 1,
    val adults: Int = 2,
    val notPrepaymentNeeded: Boolean = true,
    val includesTaxes: Boolean = true,
) {
    init {
        require(nights >= 1) { "Количество ночей должно быть не менее 1" }
        require(adults >= 1) { "Количество взрослых должно быть не менее 1" }
    }

    val hasDiscount: Boolean
        get() = discountedPrice < oldPrice

}

data class RoomData(
    val hotelData: HotelData,
    val beds: Int = 1,
) {
    init {
        require(beds >= 1) { "Количество кроватей должно быть не менее 1" }
    }
}

data class TradeOfferData(
    val roomData: RoomData,
    val priceDetailData: PriceDetailData,
    val offerStateData: OfferStateData,
    val isGeniusRecommended: Boolean = true,
) {

    // Вторичный конструктор, принимающий все аргументы "в ряд"
    constructor(
        // Данные отеля (для Hotel -> RoomData)
        hotelName: String,

        score: Float,
        reviews: Int = 0,

        location: String,
        distanceFromCentre: Int,

        promoTagKey: String = "",

        beds: Int = 1,
        nights: Int = 1,
        adults: Int = 2,

        oldPrice: Int,
        discountedPrice: Int = oldPrice,
        currency: Char = '€',

        notPrepaymentNeeded: Boolean,

        remaining: Int = 1,
        includesTaxes: Boolean = true,

        certified: Boolean = true,
        isGeniusRecommended: Boolean = true
    ) : this(
        // Собираем объект RoomData (который внутри создает Hotel)
        roomData = RoomData(
            hotelData = HotelData(hotelName, location, distanceFromCentre, certified),
            beds = beds
        ),
        // Собираем объект PriceDetail
        priceDetailData = PriceDetailData(
            oldPrice, discountedPrice, currency,
            nights, adults,
            notPrepaymentNeeded, includesTaxes
        ),
        // Собираем объект OfferState
        offerStateData = OfferStateData(score, reviews, remaining, promoTagKey),
        isGeniusRecommended = isGeniusRecommended
    )
}




