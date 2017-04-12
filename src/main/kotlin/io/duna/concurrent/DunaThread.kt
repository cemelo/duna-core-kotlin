package io.duna.concurrent

class DunaThread(name: String, task: Runnable) : Thread(task, name)
