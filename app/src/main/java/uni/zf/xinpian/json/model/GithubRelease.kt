package uni.zf.xinpian.json.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GitHub Release 主数据类
 */
@Serializable
data class GithubRelease(
    @SerialName("url") val url: String,
    @SerialName("assets_url") val assetsUrl: String,
    @SerialName("upload_url") val uploadUrl: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("id") val id: Long,
    @SerialName("author") val author: GithubUser,
    @SerialName("node_id") val nodeId: String,
    @SerialName("tag_name") val tagName: String,
    @SerialName("target_commitish") val targetCommitish: String,
    @SerialName("name") val name: String,
    @SerialName("draft") val draft: Boolean,
    @SerialName("immutable") val immutable: Boolean,
    @SerialName("prerelease") val prerelease: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("published_at") val publishedAt: String,
    @SerialName("assets") val assets: List<GithubAsset>,
    @SerialName("tarball_url") val tarballUrl: String,
    @SerialName("zipball_url") val zipballUrl: String,
    @SerialName("body") val body: String
)

/**
 * GitHub 用户信息数据类（author/uploader 共用）
 */
@Serializable
data class GithubUser(
    @SerialName("login") val login: String,
    @SerialName("id") val id: Long,
    @SerialName("node_id") val nodeId: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("gravatar_id") val gravatarId: String,
    @SerialName("url") val url: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("followers_url") val followersUrl: String,
    @SerialName("following_url") val followingUrl: String,
    @SerialName("gists_url") val gistsUrl: String,
    @SerialName("starred_url") val starredUrl: String,
    @SerialName("subscriptions_url") val subscriptionsUrl: String,
    @SerialName("organizations_url") val organizationsUrl: String,
    @SerialName("repos_url") val reposUrl: String,
    @SerialName("events_url") val eventsUrl: String,
    @SerialName("received_events_url") val receivedEventsUrl: String,
    @SerialName("type") val type: String,
    @SerialName("user_view_type") val userViewType: String,
    @SerialName("site_admin") val siteAdmin: Boolean
)

/**
 * GitHub Release 附件数据类
 */
@Serializable
data class GithubAsset(
    @SerialName("url") val url: String,
    @SerialName("id") val id: Long,
    @SerialName("node_id") val nodeId: String,
    @SerialName("name") val name: String,
    @SerialName("label") val label: String?, // 可空字段
    @SerialName("uploader") val uploader: GithubUser,
    @SerialName("content_type") val contentType: String,
    @SerialName("state") val state: String,
    @SerialName("size") val size: Long,
    @SerialName("digest") val digest: String,
    @SerialName("download_count") val downloadCount: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("browser_download_url") val browserDownloadUrl: String
)