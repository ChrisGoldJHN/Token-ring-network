import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class myServer implements Runnable{
    private ServerSocket ss;
    private JTextArea jta;
    private JLabel jl;

    public myServer(ServerSocket ss, JTextArea jta, JLabel jl) {
        this.ss = ss;
        this.jta = jta;
        this.jl = jl;
    }

    @Override
    public void run() {
        while(true){
            Socket s;
            boolean flag = false; //记录发出的数据是否回到本机
            try{
                s = ss.accept();
                int port = s.getLocalPort();  //本机端口
                int x = (port - 10000) + 1;   //本机主机号
                port = 10000 + x % 8;         //下一个主机端口号

                Socket s1 = new Socket("192.168.1.109",port);
                PrintWriter out = new PrintWriter(s1.getOutputStream(),true);

                BufferedReader bufIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String str = null;
                //读取收到的数据
                while((str = bufIn.readLine()) != null){
                    if(str.equals("Token")){  //若收到令牌，需要判断文件夹中是否存在待发的数据
                        this.jl.setBorder(BorderFactory.createLineBorder(Color.RED));
                        String fileName = "data" + String.valueOf(x) + ".txt";
                        InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(fileName)));
                        BufferedReader br = new BufferedReader(reader);
                        String fileContent = "";
                        fileContent = br.readLine();
                        if(fileContent == null){   //文件中无待发的数据，间隔一秒转发令牌
                            try{
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            out.println("Token");
                            this.jl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                        }else{  //文件中有待发的数据，发送数据，传输过程为2秒
                            send(fileName,s1,x);
                            try{
                                Thread.sleep(2000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }else{
                        /*
                            若收到数据，判断
                            1、本机发出的数据，需要重新发送令牌；
                            2、本机接收的数据，接收后重新转发数据；
                            3、本机不接受的数据，直接转发
                         */
                        String []info = str.split(":");
                        if(info[0].equals(String.valueOf(x))){  //本机发出的数据，需要重新发送令牌
                            flag = true;
                        }else if(info[1].equals(String.valueOf(x))){    //本机接收的数据，接收后重新转发数据
                            this.jta.append(info[0] + ":" + info[2] + "\r\n");
                            out.println(str);
                        }else{  //本机不接受的数据，直接转发
                            out.println(str);
                        }
                    }
                }
                if(flag){
                    out.println("Token");
                    this.jl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    flag = false;
                }
                s1.shutdownOutput();
                s1.close();
                s.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String fileName,Socket s,int num) throws IOException{
        PrintWriter out = new PrintWriter(s.getOutputStream(),true);

        InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(fileName)));
        BufferedReader br = new BufferedReader(reader);
        String fileContent = "";
        fileContent = br.readLine();
        while(fileContent != null){
            out.println(fileContent);
            fileContent = br.readLine();
        }
        s.shutdownOutput();
        s.close();

        /*
            清空文件
         */
        FileWriter fw = new FileWriter("data"+String.valueOf(num)+".txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");
        bw.flush(); //把缓冲区内容写入文件中
        bw.close();
    }
}
