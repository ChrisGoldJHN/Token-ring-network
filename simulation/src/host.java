import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class host {
    public void creat(int num){
        /*
            UI设计
        */

        //顶层容器JFrame
        JFrame jf = new JFrame("主机"+String.valueOf(num));
        if(num < 5){
            jf.setLocation(350*num,100);
        }else{
            jf.setLocation(350*(num-4),550);
        }
        jf.setSize(300,400);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);

        //中间容器JPanel
        JPanel jp = new JPanel();
        jp.setBounds(0,0,300,400);
        jp.setBackground(Color.WHITE);
        jp.setLayout(null);
        jf.add(jp);

        //标签JLabel_1
        JLabel jl_1 = new JLabel("随机生成目的主机号");
        jl_1.setBounds(50,20,200,40);
        jl_1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        jp.add(jl_1);

        //标签JLabel_2
        JLabel jl_2 = new JLabel("          令牌");
        jl_2.setBounds(100,80,100,20);
        jl_2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        jp.add(jl_2);

        //滚动容器和文本域1
        JScrollPane jsp_1 = new JScrollPane();
        jsp_1.setBounds(10,120,150,40);
        jp.add(jsp_1);

        JTextArea jta_1 = new JTextArea();
        jta_1.setLineWrap(true);
        jta_1.setForeground(Color.BLACK);
        jta_1.setFont(new Font("楷体",Font.BOLD,16));
        jsp_1.setViewportView(jta_1);

        //产生一个初始发送内容
        Random r = new Random();
        int destination = r.nextInt(8) + 1;
        String content = num + ":" + destination + ":HelloBaby";
        jta_1.setText(content);

        int delay = 6000;
        ActionListener performer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //随机生成目的主机号并显示发送内容
                Random r = new Random();
                int destination = r.nextInt(8) + 1;
                String content = num + ":" + destination + ":HelloBaby";
                jta_1.setText(content);
            }
        };
        //每隔六秒重新生成随机数
        new Timer(delay,performer).start();

        //发送按钮JButton
        JButton jb = new JButton("发送");
        jb.setBounds(170,125,100,30);
        jp.add(jb);

        //滚动容器和文本域2
        JScrollPane jsp_2 = new JScrollPane();
        jsp_2.setBounds(10,180,260,100);
        jp.add(jsp_2);

        JTextArea jta_2 = new JTextArea();
        jsp_2.setViewportView(jta_2);

        jf.setVisible(true);

        //---------------------------------------------------------

        //为发送按钮添加事件处理

        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = jta_1.getText();
                str += "\r\n";
                try{
                    write(str,num);
                }catch(IOException e1){
                    e1.printStackTrace();
                }
            }
        });

        /*
            初始时8号主机发送一个令牌给1号主机，然后令牌开始循环
         */
        if(num == 8){
            try {
                send();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        /*
            收发消息
         */
        try{
            setServer(10000 + num -1,jta_2,jl_2);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    /*
        将发送数据先写入到文件中，拿到令牌后再发送
     */
    public void write(String str,int num) throws IOException{
        FileWriter fw = new FileWriter("data"+String.valueOf(num)+".txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(str);
        bw.flush(); //把缓冲区内容写入文件中
        bw.close();
    }

    /*
        8号主机发送一个令牌给1号主机
     */
    public void send() throws IOException{
        Socket s =new Socket("192.168.1.109",10000);
        PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
        pw.println("Token");
        s.shutdownOutput();
        s.close();
    }

    public void setServer(int port,JTextArea jta,JLabel jl) throws IOException {
        ServerSocket ss = new ServerSocket(port);
        new Thread(new myServer(ss,jta,jl)).start();
    }
}