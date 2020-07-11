import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class host07 {
    public static void main(String[] args) {
        //顶层容器JFrame
        JFrame jf = new JFrame("请选择主机号");
        jf.setSize(400,150);
        jf.setLocation(800,400);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);

        //中间容器JPanel
        JPanel jp = new JPanel();
        jp.setBounds(0,0,400,150);
        jp.setBackground(Color.WHITE);
        jp.setLayout(null);
        jf.add(jp);

        //下拉列表
        JComboBox<String> jcb = new JComboBox<>();
        jcb.setBounds(50,10,300,20);
        jcb.addItem("请选择");
        jcb.addItem("1");
        jcb.addItem("2");
        jcb.addItem("3");
        jcb.addItem("4");
        jcb.addItem("5");
        jcb.addItem("6");
        jcb.addItem("7");
        jcb.addItem("8");
        jp.add(jcb);

        //确定按钮
        JButton jb = new JButton("确定");
        jb.setBounds(150,50,100,50);
        jp.add(jb);

        jf.setVisible(true);

        //为发送按钮添加事件处理
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String)jcb.getSelectedItem();
                if(selected != null){
                    int num = Integer.parseInt(selected);
                    jf.setVisible(false);
                    host h1 = new host();
                    h1.creat(num);
                }
            }
        });
    }
}
