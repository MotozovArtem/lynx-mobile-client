package org.lynx.client.service

interface UserService {
    var authenticatedUser: String
}

class UserServiceImpl(override var authenticatedUser: String) : UserService