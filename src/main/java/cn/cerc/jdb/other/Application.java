package cn.cerc.jdb.other;

import java.awt.EventQueue;

/**
 * 用于建立客户端窗口
 * 
 * @author 张弓
 *
 */
public class Application {
	private CustomForm mainForm;

	public CustomForm createForm(Class<?> clazz) {
		try {
			mainForm = (CustomForm) clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return mainForm;
	}

	public void run() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainForm.setVisible(true);
			}
		});
	}

	public CustomForm getMainForm() {
		return mainForm;
	}

	public void setMainForm(CustomForm mainForm) {
		this.mainForm = mainForm;
	}

	public static void main(String[] args) {
		Application app = new Application();
		app.createForm(CustomForm.class);
		app.run();
	}

}
