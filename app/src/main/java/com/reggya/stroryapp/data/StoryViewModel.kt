package com.reggya.stroryapp.data

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import java.io.File

class StoryViewModel (private val useCase: IRepository): ViewModel() {

    fun register(registerResponse: RegisterResponse) = LiveDataReactiveStreams.fromPublisher(useCase.register(registerResponse))
    fun login(registerResponse: RegisterResponse) = LiveDataReactiveStreams.fromPublisher(useCase.login(registerResponse))
    fun getStories(token: String) = LiveDataReactiveStreams.fromPublisher(useCase.getStories(token))
    fun addStory(token: String, file: File, description: String) = LiveDataReactiveStreams.fromPublisher(useCase.addStories(token, file, description))
}