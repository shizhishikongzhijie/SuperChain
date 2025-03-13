//package com.example.SuperChain.util.grpc;
//
//
//// 自定义重试拦截器
//import io.grpc.CallOptions;
//import io.grpc.Channel;
//import io.grpc.ClientCall;
//import io.grpc.ClientInterceptor;
//import io.grpc.ForwardingClientCall;
//import io.grpc.Metadata;
//import io.grpc.MethodDescriptor;
//import io.grpc.Status;
//import io.grpc.StatusRuntimeException;
//
//import java.util.concurrent.TimeUnit;
//
//public class RetryInterceptor implements ClientInterceptor {
//    private final RetrySettings retrySettings;
//
//    public RetryInterceptor(RetrySettings retrySettings) {
//        this.retrySettings = retrySettings;
//    }
//
//    @Override
//    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
//                                                               CallOptions callOptions, Channel next) {
//        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
//            @Override
//            public void start(Listener<RespT> responseListener, Metadata headers) {
//                super.start(new Listener<RespT>() {
//                    @Override
//                    public void onMessage(RespT message) {
//                        responseListener.onMessage(message);
//                    }
//
//                    @Override
//                    public void onClose(Status status, Metadata trailers) {
//                        if (status.getCode() == Status.Code.UNAVAILABLE) {
//                            retry(status, trailers, responseListener);
//                        } else {
//                            responseListener.onClose(status, trailers);
//                        }
//                    }
//                }, headers);
//            }
//
//            private void retry(Status status, Metadata trailers, Listener<RespT> responseListener) {
//                long delay = retrySettings.getInitialRetryDelayMillis();
//                int attempt = 1;
//                while (attempt <= retrySettings.getMaxAttempts()) {
//                    try {
//                        Thread.sleep(delay);
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        responseListener.onClose(status, trailers);
//                        return;
//                    }
//
//                    try {
//                        super.start(responseListener, trailers);
//                        return;
//                    } catch (StatusRuntimeException e) {
//                        if (e.getStatus().getCode() != Status.Code.UNAVAILABLE) {
//                            responseListener.onClose(e.getStatus(), trailers);
//                            return;
//                        }
//                        delay = (long) (delay * retrySettings.getRetryDelayMultiplier());
//                        attempt++;
//                    }
//                }
//                responseListener.onClose(status, trailers);
//            }
//        };
//    }
//}
