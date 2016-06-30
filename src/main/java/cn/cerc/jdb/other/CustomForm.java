package cn.cerc.jdb.other;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 窗口基类
 * 
 * @author 张弓
 *
 */
public class CustomForm extends JFrame {
	private static final long serialVersionUID = 1L;
	protected JPanel contentPanel = new JPanel();

	public CustomForm() {
		this.setTitle("(未命名)");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		this.setSize(screenSize.width / 2, screenSize.height / 2);

		this.setContentPane(contentPanel);
	}

}
