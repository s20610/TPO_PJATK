/**
 *
 *  @author Kotnowski Borys S20610
 *
 */
package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServer {
    public InetSocketAddress iSA;
    public StringBuilder serverLogger;
    public Lock locker = new ReentrantLock();
    public Map<SocketChannel, String> clMap;
    public Thread serverThread;
    public Selector select;
    public ServerSocketChannel sSC;

    public ChatServer(String host, int port) {
        clMap = new HashMap<>();
        iSA = new InetSocketAddress(host, port);
        serverThread = serverThread();
        serverLogger = new StringBuilder();
    }
    private StringBuilder requestHandler(SocketChannel cSocket, String s) {
        StringBuilder cResponse = new StringBuilder();
        try {
            if (s.matches("log in .+")) {
                clMap.put(cSocket, s.substring(7));
                serverLogger.append(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"))).append(" ").append(s.substring(7)).append(" logged in").append("\n");
                cResponse.append(s.substring(7)).append(" logged in").append("\n");

            } else if (s.matches("log out")) {
                serverLogger.append(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"))).append(" ").append(clMap.get(cSocket)).append(" logged out").append("\n");
                cResponse.append(clMap.get(cSocket)).append(" logged out").append("\n");

                ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(cResponse.toString());
                cSocket.write(byteBuffer);

                clMap.remove(cSocket);
            } else {
                serverLogger.append(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"))).append(" ").append(clMap.get(cSocket)).append(": ").append(s).append("\n");
                cResponse.append(clMap.get(cSocket)).append(": ").append(s).append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cResponse;
    }
    private Thread serverThread() {
        return new Thread(() -> {
            try {
                select = Selector.open();
                sSC = ServerSocketChannel.open();
                sSC.configureBlocking(false);
                sSC.bind(iSA);
                sSC.register(select, sSC.validOps(), null);
                while (!serverThread.isInterrupted()) {
                    select.select();
                    if (serverThread.isInterrupted()){
                        break;
                    }
                    Iterator<SelectionKey> iter = select.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey sKey = iter.next();
                        iter.remove();
                        if (sKey.isAcceptable()) {
                            SocketChannel clientSocket = sSC.accept();
                            clientSocket.configureBlocking(false);
                            clientSocket.register(select, SelectionKey.OP_READ);
                        }
                        if (sKey.isReadable()) {
                            SocketChannel cSocket = (SocketChannel) sKey.channel();
                            int bufferCapacity = 1024;
                            ByteBuffer buffer = ByteBuffer.allocateDirect(bufferCapacity);
                            StringBuilder cRequest = new StringBuilder();
                            int readBytes = 0;
                            do {
                                try {
                                    locker.lock();
                                    readBytes = cSocket.read(buffer);
                                    buffer.flip();
                                    cRequest.append(StandardCharsets.UTF_8.decode(buffer));
                                    buffer.clear();
                                    readBytes = cSocket.read(buffer);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    locker.unlock();
                                }
                            } while (readBytes != 0);
                            String[] records = cRequest.toString().split(";");
                            for (String record : records) {
                                String cResponse = requestHandler(cSocket, record).toString();
                                for (Map.Entry<SocketChannel, String> entry : clMap.entrySet()) {
                                    ByteBuffer bB = StandardCharsets.UTF_8.encode(cResponse);
                                    entry.getKey().write(bB); } } } } }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
    String getServerLog() {
        return serverLogger.toString();
    }
    public void stopServer() {
        try {
            locker.lock();
            serverThread.interrupt();
            select.close();
            sSC.close();
            System.out.println("Server stopped");
        } catch (IOException exception) {
            exception.printStackTrace();
        }finally {
            locker.unlock();
        }
    }
    public void startServer() {
        serverThread.start();
        System.out.println("Server started"+"\n");
    }
}


