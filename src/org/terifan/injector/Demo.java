package org.terifan.injector;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;


public class Demo
{
	public static void main(String... args)
	{
		try
		{
			Injector injector = new Injector();

			// normal running
//			injector.bind(UserService.class).asSingleton();

			// when developing & testing
			injector.bind(UserService.class).toInstance(new MockUserService(new User("dave", "asasasasas asasasasas"), new User("steve", "ghghghghgh ghghghgh ghghghgh")));

			injector.bind(Style.class).toInstance(new Style(Color.RED, Color.BLUE));

			// bind()
			// bind().asSingleton()
			// bind().to()
			// bind().to().asSingleton()
			// bind().to().in()
			// bind().to().in().asSingleton()
			// bind().toInstance()
			// bind().toInstance().in()
			// bind().toProvider()
			// bind().toProvider().in()
			// bind().named()
			// bind().named().asSingleton()
			// bind().named().to()
			// bind().named().to().asSingleton()
			// bind().named().to().in()
			// bind().named().to().in().asSingleton()
			// bind().named().toInstance()
			// bind().named().toInstance().in()
			// bind().named().toProvider()
			// bind().named().toProvider().in()

			UserPanel panel = injector.getInstance(UserPanel.class);

			JFrame frame = new JFrame();
			frame.add(panel);
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	static class Style
	{
		Color mText;
		Color mBackground;


		public Style()
		{
			this(Color.BLACK, Color.WHITE);
		}


		public Style(Color aText, Color aBackground)
		{
			mText = aText;
			mBackground = aBackground;
		}
	}


	static class UserPanel extends JPanel
	{
		@Inject private UserService mUserService;
		@Inject private Style mStyle;
		private User mUser;


		@PostConstruct
		public void buildForm()
		{
			JList<User> list = new JList<>(mUserService.getUsers());
			JTextArea text = new JTextArea();

			text.setForeground(mStyle.mText);
			text.setBackground(mStyle.mBackground);

			list.addListSelectionListener(aEvent ->
			{
				if (mUser != null && !mUser.mDescription.equals(text.getText()))
				{
					mUser.mDescription = text.getText();
					mUserService.save(mUser);
				}

				mUser = list.getModel().getElementAt(list.getSelectedIndex());

				text.setText(mUser.mDescription);
			});

			super.setLayout(new BorderLayout());
			super.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(list), new JScrollPane(text)));
		}
	}


	static class MockUserService extends UserService
	{
		User[] mUsers;


		public MockUserService()
		{
		}


		public MockUserService(User... aUsers)
		{
			mUsers = aUsers;
		}


		@Override
		public User[] getUsers()
		{
			return mUsers;
		}


		@Override
		public void save(User aUser)
		{
			System.out.println("saved user: " + aUser + "=" + aUser.mDescription);
		}
	}


	static class UserService
	{
		User[] getUsers()
		{
			throw new UnsupportedOperationException();
		}


		void save(User aUser)
		{
			throw new UnsupportedOperationException();
		}
	}


	static class User
	{
		String mName;
		String mDescription;


		public User(String aName, String aDescription)
		{
			mName = aName;
			mDescription = aDescription;
		}


		@Override
		public String toString()
		{
			return mName;
		}
	}
}
