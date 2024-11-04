package com.diegoalvis.travelapp

import io.grpc.*

class LoggingInterceptor : ClientInterceptor {
    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            next.newCall(method, callOptions)
        ) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                println("Starting call to ${method.fullMethodName}")
                super.start(object : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    override fun onMessage(message: RespT) {
                        println("Received message: $message")
                        super.onMessage(message)
                    }

                    override fun onClose(status: Status, trailers: Metadata) {
                        println("Call closed with status: $status")
                        super.onClose(status, trailers)
                    }
                }, headers)
            }
        }
    }
}