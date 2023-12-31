package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.troplo.privateuploader.api.TpuFunctions

@JsonClass(generateAdapter = true)
@Keep
data class User(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "administrator") val administrator: Boolean,
    @field:Json(name = "darkTheme") val darkTheme: Boolean,
    @field:Json(name = "emailVerified") val emailVerified: Boolean,
    @field:Json(name = "banned") val banned: Boolean,
    @field:Json(name = "inviteId") val inviteId: Int?,
    @field:Json(name = "openGraph") val openGraph: Any?,
    @field:Json(name = "discordPrecache") val discordPrecache: Boolean,
    @field:Json(name = "avatar") val avatar: String?,
    @field:Json(name = "subdomainId") val subdomainId: Int?,
    @field:Json(name = "domainId") val domainId: Int?,
    @field:Json(name = "totpEnable") val totpEnable: Boolean,
    @field:Json(name = "quota") val quota: Long?,
    @field:Json(name = "uploadNameHidden") val uploadNameHidden: Boolean?,
    @field:Json(name = "invisibleURLs") val invisibleURLs: Boolean?,
    @field:Json(name = "moderator") val moderator: Boolean,
    @field:Json(name = "subscriptionId") val subscriptionId: Int,
    @field:Json(name = "fakePath") val fakePath: Any?,
    @field:Json(name = "themeId") val themeId: Any?,
    @field:Json(name = "itemsPerPage") val itemsPerPage: Int,
    @field:Json(name = "banner") val banner: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "storedStatus") val storedStatus: String,
    @field:Json(name = "weatherUnit") val weatherUnit: String?,
    @field:Json(name = "emailToken") val emailToken: String?,
    @field:Json(name = "insights") val insights: String?,
    @field:Json(name = "excludedCollections") val excludedCollections: List<Int>?,
    @field:Json(name = "language") val language: String?,
    @field:Json(name = "publicProfile") val publicProfile: Boolean?,
    @field:Json(name = "xp") val xp: Int?,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
    @field:Json(name = "planId") val planId: Int?,
    @field:Json(name = "experiments") val experiments: List<Experiment>?,
    @field:Json(name = "subscription") val subscription: Any?,
    @field:Json(name = "domain") val domain: Domain,
    @field:Json(name = "plan") val plan: Plan,
    @field:Json(name = "badges") val badges: List<Badge>,
    @field:Json(name = "integrations") val integrations: List<Any>,
    @field:Json(name = "scopes") val scopes: String?,
    @field:Json(name = "pendingAutoCollects") val pendingAutoCollects: Int,
    @field:Json(name = "notifications") var notifications: List<Notification>,
    @field:Json(name = "platforms") val platforms: List<Platform>?,
    @field:Json(name = "nickname") val nickname: Nickname? = null,
    @field:Json(name = "bot") val bot: Boolean? = null,
)

@JsonClass(generateAdapter = true)
@Keep
data class Platform(
    @field:Json(name = "platform") val platform: String,
    @field:Json(name = "id") val id: String,
    @field:Json(name = "lastSeen") val lastSeen: String,
    @field:Json(name = "status") val status: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class Experiment(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "key") val key: String,
    @field:Json(name = "value") val value: String,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class Domain(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "domain") val domain: String,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "DNSProvisioned") val DNSProvisioned: Boolean,
    @field:Json(name = "active") val active: Boolean,
    @field:Json(name = "zone") val zone: String?,
    @field:Json(name = "advanced") val advanced: String,
    @field:Json(name = "subdomains") val subdomains: Boolean,
    @field:Json(name = "subdomainsCreate") val subdomainsCreate: Boolean,
    @field:Json(name = "customUserEligibility") val customUserEligibility: String?,
    @field:Json(name = "restricted") val restricted: String,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class Plan(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "quotaMax") val quotaMax: Long,
    @field:Json(name = "price") val price: Int,
    @field:Json(name = "features") val features: String,
    @field:Json(name = "color") val color: String,
    @field:Json(name = "internalName") val internalName: String,
    @field:Json(name = "purchasable") val purchasable: Boolean,
    @field:Json(name = "internalFeatures") val internalFeatures: String,
    @field:Json(name = "icon") val icon: String,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class Badge(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "tooltip") val tooltip: String,
    @field:Json(name = "image") val image: String?,
    @field:Json(name = "icon") val icon: String,
    @field:Json(name = "color") val color: String,
    @field:Json(name = "unlocked") val unlocked: Boolean,
    @field:Json(name = "priority") val priority: Int,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
    @field:Json(name = "planId") val planId: String?,
    @field:Json(name = "BadgeAssociation") val badgeAssociation: BadgeAssociation,
)

@JsonClass(generateAdapter = true)
@Keep
data class BadgeAssociation(
    @field:Json(name = "badgeId") val badgeId: Int,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "expiredAt") val expiredAt: String?,
    @field:Json(name = "hidden") val hidden: Boolean,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class Notification(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "dismissed") var dismissed: Boolean,
    @field:Json(name = "route") val route: String?,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
)

// default new user
fun defaultUser() = User(
    id = 1,
    username = "Test",
    email = "troplo@troplo.com",
    description = "Hey, I'm Troplo, owner of TPU!\nDiscord: Troplo#8495",
    administrator = true,
    darkTheme = true,
    emailVerified = true,
    banned = false,
    inviteId = 1,
    openGraph = 0,
    discordPrecache = true,
    avatar = "a.png",
    subdomainId = null,
    domainId = 1,
    totpEnable = true,
    quota = 29967784171,
    uploadNameHidden = false,
    invisibleURLs = false,
    moderator = false,
    subscriptionId = 1,
    fakePath = 0,
    themeId = 1,
    itemsPerPage = 24,
    banner = "b.png",
    status = "offline",
    storedStatus = "online",
    weatherUnit = "celsius",
    emailToken = null,
    insights = "everyone",
    excludedCollections = listOf(175, 51, 176),
    language = "en",
    publicProfile = true,
    xp = 0,
    createdAt = "2021-10-05T12:02:18.000Z",
    updatedAt = "2023-06-25T14:44:59.000Z",
    planId = 6,
    experiments = listOf(
        Experiment(
            id = 8,
            key = "WEBMAIL",
            value = "true",
            userId = 1,
            createdAt = "2023-06-19T01:11:13.000Z",
            updatedAt = "2023-06-19T01:11:13.000Z"
        )
    ),
    subscription = null,
    domain = Domain(
        id = 1,
        domain = "i.troplo.com",
        userId = 1,
        DNSProvisioned = true,
        active = true,
        zone = null,
        advanced = "0",
        subdomains = false,
        subdomainsCreate = false,
        customUserEligibility = null,
        restricted = "disabled",
        createdAt = "2021-10-05T23:06:44.000Z",
        updatedAt = "2021-10-05T23:06:44.000Z"
    ),
    plan = Plan(
        id = 6,
        name = "Gold",
        quotaMax = 75866302316544,
        price = 0,
        features = "[\"Unlimited Storage\", \"Unlimited File Size Limit\", \"4 invites per wave\"]",
        color = "#FFD700",
        internalName = "GOLD",
        purchasable = false,
        internalFeatures = "{\"maxFileSize\": 9223372036854775805, \"invites\": 4}",
        icon = "mdi-plus",
        createdAt = "2021-08-30T15:39:23.000Z",
        updatedAt = "2021-08-30T15:39:23.000Z"
    ),
    badges = listOf(
        Badge(
            id = 11,
            name = "TPU Developer",
            description = "TPU Developer",
            tooltip = "Contributed to TPU",
            image = null,
            icon = "mdi-code-tags",
            color = "#ffd700",
            unlocked = true,
            priority = 69,
            createdAt = "2023-03-05T00:00:00.000Z",
            updatedAt = "2023-03-05T13:08:57.000Z",
            planId = null,
            badgeAssociation = BadgeAssociation(
                badgeId = 11,
                userId = 1,
                expiredAt = null,
                hidden = false,
                createdAt = "2023-03-05T15:41:31.000Z",
                updatedAt = "2023-03-05T15:41:31.000Z"
            )
        )
    ),
    integrations = emptyList(),
    scopes = "*",
    pendingAutoCollects = 0,
    notifications = listOf(
        Notification(
            id = 4649,
            message = "Spy_Testing has sent you a friend request!",
            userId = 1,
            dismissed = false,
            route = "/u/Spy_Testing",
            createdAt = "2023-06-14T01:08:10.000Z",
            updatedAt = "2023-06-23T00:22:37.000Z"
        )
    ),
    platforms = emptyList()
)

@JsonClass(generateAdapter = true)
@Keep
data class StatusPayload(
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "platforms") val platforms: List<Platform>?,
)

@JsonClass(generateAdapter = true)
@Keep
data class PartialUser(
    @field:Json(name = "avatar") val avatar: String?,
    @field:Json(name = "banner") val banner: String?,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "nickname") val nickname: Nickname?,
    @field:Json(name = "plan") val plan: Plan?,
    @field:Json(name = "platforms") val platforms: List<Platform>?,
    @field:Json(name = "status") var status: String?,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "administrator") val administrator: Boolean?,
    @field:Json(name = "moderator") val moderator: Boolean?
)

@JsonClass(generateAdapter = true)
@Keep
data class PartialUserSocket(
    @field:Json(name = "avatar") val avatar: String?,
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "username") val username: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class Nickname(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
    @field:Json(name = "user") val user: PartialUser?,
    @field:Json(name = "friend") val friend: PartialUser?,
    @field:Json(name = "friendId") val friendId: Int?,
)

fun defaultPartialUser(
    username: String? = "Troplo",
    avatar: String? = "50ba79e4.png",
    id: Int? = 1,
) = PartialUser(
    id = id ?: 1,
    username = username ?: "Troplo",
    description = "Hey, I'm Troplo, owner of TPU!\nDiscord: Troplo#8495",
    administrator = true,
    avatar = avatar ?: "50ba79e4.png",
    moderator = false,
    banner = "b.png",
    status = "online",
    plan = Plan(
        id = 6,
        name = "Gold",
        quotaMax = 75866302316544,
        price = 0,
        features = "[\"Unlimited Storage\", \"Unlimited File Size Limit\", \"4 invites per wave\"]",
        color = "#FFD700",
        internalName = "GOLD",
        purchasable = false,
        internalFeatures = "{\"maxFileSize\": 9223372036854775805, \"invites\": 4}",
        icon = "mdi-plus",
        createdAt = "2021-08-30T15:39:23.000Z",
        updatedAt = "2021-08-30T15:39:23.000Z"
    ),
    platforms = listOf(
        Platform(
            platform = "android_kotlin",
            id = "test",
            lastSeen = TpuFunctions.currentISODate(),
            status = "online"
        )
    ),
    nickname = null
)

@JsonClass(generateAdapter = true)
@Keep
data class SettingsPayload(
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "discordPrecache") val discordPrecache: Boolean?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "excludedCollections") val excludedCollections: List<Int>?,
    @field:Json(name = "insights") val insights: String?,
    @field:Json(name = "itemsPerPage") val itemsPerPage: Int?,
    @field:Json(name = "language") val language: String?,
    // TODO: Profile Layout type, not currently parsed or used by Kotlin
    @field:Json(name = "profileLayout") val profileLayout: Any?,
    @field:Json(name = "publicProfile") val publicProfile: Boolean?,
    @field:Json(name = "storedStatus") val storedStatus: String?,
    // TODO: Theme Engine type, not currently parsed or used by Kotlin
    @field:Json(name = "themeEngine") val themeEngine: Any?,
    @field:Json(name = "weatherUnit") val weatherUnit: String?,
)

@JsonClass(generateAdapter = true)
@Keep
data class PatchUser(
    @field:Json(name = "username") val username: String? = null,
    @field:Json(name = "description") val description: String? = null,
    @field:Json(name = "email") val email: String? = null,
    @field:Json(name = "language") val language: String? = null,
    @field:Json(name = "weatherUnit") val weatherUnit: String? = null,
    @field:Json(name = "publicProfile") val publicProfile: Boolean? = null,
    @field:Json(name = "discordPrecache") val discordPrecache: Boolean? = null,
    @field:Json(name = "storedStatus") val storedStatus: String? = null,
    @field:Json(name = "excludedCollections") val excludedCollections: List<Int>? = null,
    @field:Json(name = "profileLayout") val profileLayout: Any? = null,
    @field:Json(name = "themeEngine") val themeEngine: Any? = null,
    @field:Json(name = "insights") val insights: String? = null,
    @field:Json(name = "itemsPerPage") val itemsPerPage: Int? = null,
    @field:Json(name = "currentPassword") val currentPassword: String? = null,
    @field:Json(name = "password") val password: String? = null,
)