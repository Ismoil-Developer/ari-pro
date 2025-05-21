package uz.mrx.aripro.utils

import android.content.Context


sealed class ResultData<out T> {
    data class Success<T>(val data: T) : ResultData<T>()
    data class Error<T>(val error: Throwable) : ResultData<T>()
    data class Message<T>(val message: MessageDataR) : ResultData<T>()

    val isSuccess = this is Success<T>
    val isError = this is Error
    val isMessage = this is Message

    inline fun onSuccess(block: (T) -> Unit): ResultData<T> {
        if (this is Success<T>) block(this.data)
        return this
    }

    inline fun onError(block: (Throwable) -> Unit): ResultData<T> {
        if (this is Error) block(this.error)
        return this
    }

    inline fun onMessage(block: (MessageDataR) -> Unit): ResultData<T> {
        if (this is Message) block(this.message)
        return this
    }

    companion object {
        fun <T> success(value: T) = Success(value)
        fun <T> error(value: Throwable) = Error<T>(value)
        fun <T> messageText(value: String) = Message<T>(MessageDataR.Text(value))
        fun <T> messageResource(value: Int) = Message<T>(MessageDataR.Resource(value))
        fun <T> message(value: MessageDataR.Text) = Message<T>(value)
    }
}

sealed class MessageDataR {
    data class Text(val text: String) : MessageDataR()
    data class Resource(val id: Int) : MessageDataR()

    val isText = this is Text
    val isResource = this is Resource

    inline fun onText(block: (String) -> Unit): MessageDataR {
        if (this is Text) block(this.text)
        return this
    }

    inline fun onResource(block: (Int) -> Unit): MessageDataR {
        if (this is Resource) block(this.id)
        return this
    }

    inline fun onMessageString(context: Context, block: (String) -> Unit): MessageDataR {
        val result = getMessageString(context)
        block(result)
        return this
    }

    fun getMessageString(context: Context): String = when (this) {
        is Text -> text
        is Resource -> context.getString(id)
    }

    companion object {
        fun messageText(value: String) = Text(value)
        fun messageResource(value: Int) = Resource(value)
    }
}