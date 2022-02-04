package com.blog.domain

import com.blog.controller.LikeOrDislikeRequest
import com.blog.controller.view.PostDetailsView
import com.blog.domain.PostStatus.DRAFT
import com.blog.domain.PostStatus.PUBLISH
import com.blog.domain.Visibility.PUBLIC
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val POST_COLLECTION = "posts"

@TypeAlias("Post")
@Document(POST_COLLECTION)
data class Post(
    @Id
    private var id: ObjectId? = null,
    @Indexed(unique = true)
    val postId: PostId,
    @Indexed(unique = true)
    private var url: String = postId,
    private var title: String = "Add new post",
    val content: Content = Content(),
    val publishedContent: Content = content,
    val postDate: PostDate = PostDate(),
    val authorId: AuthorId,
    val visibility: Visibility = PUBLIC,
    private var postStatus: PostStatus = DRAFT,
    private var commentsAllowed: Boolean = true,
    val categories: MutableSet<CategoryId> = mutableSetOf(),
    val tags: MutableSet<TagId> = mutableSetOf(),
    val likes: MutableSet<UserId> = mutableSetOf(),
    val dislikes: MutableSet<UserId> = mutableSetOf()
) {
    fun update(postDetailsView: PostDetailsView): Post {
        url = postDetailsView.url
        title = postDetailsView.title
        commentsAllowed = postDetailsView.commentsAllowed
        tags.addAll(postDetailsView.tags)
        categories.addAll(postDetailsView.categories)
        if (postDetailsView.postStatus == PUBLISH) {
            postStatus = PUBLISH
            postDate.publish(postStatus)
            publishedContent.update(content)
        } else {
            content.update(postDetailsView.content)
        }
        return this
    }

    fun updateLikeOrDislike(likeOrDislikeRequest: LikeOrDislikeRequest, userId: String): Post {
        when (likeOrDislikeRequest.action) {
            Action.ADD_LIKE -> {
                likes.add(userId)
                dislikes.removeIf { it == userId }
            }
            Action.ADD_DISLIKE -> {
                dislikes.add(userId)
                likes.removeIf { it == userId }
            }
            Action.REMOVE_LIKE -> likes.removeIf { it == userId }
            Action.REMOVE_DISLIKE -> dislikes.removeIf { it == userId }
        }
        return this
    }

    fun getUrl() = this.url
    fun getTitle() = this.title
    fun getStatus() = this.postStatus
    fun isCommentsAllowed() = this.commentsAllowed
}

data class PostDate(
    private var publishedOn: LocalDateTime? = null,
    private var lastUpdateOn: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun publish(postStatus: PostStatus) {
        if (postStatus == DRAFT) {
            publishedOn = LocalDateTime.now()
        }
        lastUpdateOn = LocalDateTime.now()
    }
}

enum class PostStatus {
    DRAFT,
    PUBLISH,
    TRASH
}

enum class Visibility {
    PUBLIC,
    PRIVATE
}

typealias PostId = String
