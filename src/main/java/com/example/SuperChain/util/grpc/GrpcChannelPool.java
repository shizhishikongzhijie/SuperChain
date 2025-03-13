//package com.example.SuperChain.util.grpc;
//
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//public class GrpcChannelPool {
//    private final ConcurrentMap<String, ManagedChannel> channelMap = new ConcurrentHashMap<>();
//    private final String serverAddress;
//    private final int maxChannels;
//    private final RetrySettings retrySettings;
//
//    public GrpcChannelPool(String serverAddress, int maxChannels) {
//        this.serverAddress = serverAddress;
//        this.maxChannels = maxChannels;
//        this.retrySettings = RetrySettings.newBuilder()
//                .setInitialRetryDelayMillis(100)
//                .setMaxRetryDelayMillis(30000)
//                .setTotalTimeoutMillis(60000)
//                .setRetryDelayMultiplier(1.3)
//                .setMaxAttempts(5)
//                .build();
//    }
//
//    public synchronized ManagedChannel getChannel() {
//        ManagedChannel channel = channelMap.get(serverAddress);
//        if (channel == null || !channel.isShutdown()) {
//            channel = createChannelWithRetry();
//            channelMap.put(serverAddress, channel);
//        }
//        return channel;
//    }
//
//    private ManagedChannel createChannelWithRetry() {
//        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(serverAddress)
//                .usePlaintext()
//                .intercept(new RetryInterceptor(retrySettings));
//        return builder.build();
//    }
//
//    public void shutdown() {
//        channelMap.values().forEach(ManagedChannel::shutdownNow);
//    }
//}
