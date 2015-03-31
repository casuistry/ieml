package UI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import NewParser.Parser;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;

public class EditorFrame extends JFrame {

	private JPanel contentPane;
    private JLabel lblNewLabel;
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EditorFrame frame = new EditorFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EditorFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 10));
		setContentPane(contentPane);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(5, 10, 0, 0));
		
		List<Character> list = new ArrayList<Character>();
		list.addAll(Parser.c_star);
		list.addAll(Parser.c_addOp);
		list.addAll(Parser.c_wLetter);
		list.addAll(Parser.c_alphabet);
		list.addAll(Parser.c_smallCap);
		list.addAll(Parser.c_marks);
		
		for (int i = 0; i < list.size(); i++) {
			JButton nb = new JButton(list.get(i).toString());
			nb.setFont(new Font("Tahoma", Font.BOLD, 16));
			nb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					handleButtonClicked(arg0);
				}
			});
			panel.add(nb);
		}
	}

	private void handleButtonClicked(ActionEvent arg0){
		
		JButton bt = (JButton)arg0.getSource();
		StringBuilder builder = new StringBuilder(lblNewLabel.getText());
		builder.append(bt.getText());	
		lblNewLabel.setText(builder.toString());
	}
}
