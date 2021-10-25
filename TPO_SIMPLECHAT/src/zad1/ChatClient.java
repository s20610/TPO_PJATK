/**
 *
 *  @author Kotnowski Borys S20610
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatClient {
    public InetSocketAddress iSA;
    public SocketChannel sChannel;
    public String cID;
    public StringBuilder cViewer;
    public Lock locker = new ReentrantLock();
    public Thread receiverThread = new Thread(this::run);

    public ChatClient(String host, int port, String cID) {
        this.iSA = new InetSocketAddress(host, port);
        this.cID = cID;
        cViewer = new StringBuilder("=== " + this.cID + " chat view\n");
    }
    public void send(String req) {
        try {
            Thread.sleep(30);
            sChannel.write(StandardCharsets.UTF_8.encode(req));
            Thread.sleep(30);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void logout() {
        send("log out");
        try {
            locker.lock();
            receiverThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            locker.unlock();
        }
    }
    public void login() {
        try {
            sChannel = SocketChannel.open(iSA);
            sChannel.configureBlocking(false);
            send("log in " + cID);
            receiverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        } }
    private void run() {
        int capacity = 1024;
        ByteBuffer bBuffer = ByteBuffer.allocateDirect(capacity);
        int bRead = 0;
        while (!receiverThread.isInterrupted()) {
            do {
                try {
                    locker.lock();
                    bRead = sChannel.read(bBuffer);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    locker.unlock();
                }
            } while (bRead == 0 && !receiverThread.isInterrupted());
            bBuffer.flip();
            String res = StandardCharsets.UTF_8.decode(bBuffer).toString();
            cViewer.append(res);
            bBuffer.clear();
        } }
    public String getChatView() {
        return cViewer.toString();
    }}
