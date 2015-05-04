package UI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import NewParser.ParserImpl;
import NewParser.Tokenizer;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.Font;

import javax.swing.JTextArea;

import java.awt.Color;

public class EditorFrame extends JFrame {

	private JPanel contentPane;
    private JLabel iemlText;
    private JButton clearButton;
    HashMap<Character, JButton> buttons = new HashMap<Character, JButton>();
    ParserImpl parser = new ParserImpl();
    private JPanel panel_1;
    private JLabel errorText;
    
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
		setBounds(100, 100, 700, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 10));
		setContentPane(contentPane);
		
		iemlText = new JLabel("");
		iemlText.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(iemlText, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(5, 10, 0, 0));
		
		panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		clearButton = new JButton("Clear");
		panel_1.add(clearButton, BorderLayout.NORTH);
		
		errorText = new JLabel("");
		errorText.setForeground(Color.RED);
		panel_1.add(errorText, BorderLayout.SOUTH);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iemlText.setText("");
				errorText.setText("");
				parser = new ParserImpl();
				
				try {		
					computeEnabled();			
				} catch (Exception ex) {
					errorText.setText(ex.getMessage());
				}
			}
		});
		
		List<Character> list = new ArrayList<Character>();
		list.addAll(Tokenizer.c_star);			
		list.addAll(Tokenizer.c_addOp);		
		list.addAll(Tokenizer.c_wLetter);		
		list.addAll(Tokenizer.c_alphabet);		
		list.addAll(Tokenizer.c_smallCap);
		list.addAll(Tokenizer.c_marks);
		list.addAll(Tokenizer.c_ignore);
		

		for (int i = 0; i < list.size(); i++) {
			
			JButton nb = new JButton(list.get(i).toString());
			nb.setFont(new Font("Tahoma", Font.BOLD, 16));
			nb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					handleButtonClicked(arg0);
				}
			});
			panel.add(nb);
			buttons.put(list.get(i), nb);
		}
		
		try {		
			computeEnabled();			
		} catch (Exception e) {
			errorText.setText(e.getMessage());
		}
	}

	private void enableButtons(List<Character> l){
		
		for (JButton b : buttons.values())
			b.setEnabled(false);
		
		for (Character c : l){
			if (buttons.containsKey(c)) 
				buttons.get(c).setEnabled(true);		
		}
	}
	
	private void computeEnabled() throws Exception {

		ArrayList<Character> l = new ArrayList<Character>();
		
		switch (parser.GetCurrentState()) {
		case state_a:
			next(Tokenizer.c_marks.get(0));
			return;
		case state_d:
			l.addAll(Tokenizer.c_wLetter);		
			l.addAll(Tokenizer.c_alphabet);		
			l.addAll(Tokenizer.c_smallCap);
			l.addAll(Tokenizer.c_ignore);
			break;
		case state_f:
			
			Integer prev = null; //parser.getPreviousLM();
			
			l.addAll(Tokenizer.c_addOp);	
			l.addAll(Tokenizer.c_ignore);
			
			/*if (parser.canMultiplyNode()){
				if (parser.canAddLayer(0))
					l.addAll(Parser.c_alphabet);
				if (parser.canAddLayer(1)) {
					l.addAll(Parser.c_wLetter);							
					l.addAll(Parser.c_smallCap);
				}
			}	*/		
			if (prev != null && prev == Tokenizer.c_marks.size()-1) {
				next(Tokenizer.c_star.get(0));
				return;
			}
			if (prev != null && prev < Tokenizer.c_marks.size()-1) 
				l.add(Tokenizer.c_marks.get(prev+1));
			/*if (parser.canMovePost())
				l.addAll(Parser.c_star);*/
			break;
		case state_i:
			l.addAll(Tokenizer.c_wLetter);		
			l.addAll(Tokenizer.c_alphabet);		
			l.addAll(Tokenizer.c_smallCap);
			l.addAll(Tokenizer.c_ignore);
			break;
		case state_sc:
			next(Tokenizer.c_marks.get(1));
			return;
		case state_ws:
			l.addAll(Tokenizer.c_vowels);
			break;
		default:
			throw new Exception("undefined state " /*+ parser.GetCurrentState().getFieldDescription()*/);
		}
		
		enableButtons(l);
	}
	
	private void handleButtonClicked(ActionEvent arg0){
		
		JButton bt = (JButton)arg0.getSource();
		next(bt.getText().charAt(0));
	}
	
	private void next(Character c) {
		
		StringBuilder builder = new StringBuilder(iemlText.getText());
		builder.append(c);	
		iemlText.setText(builder.toString());
		
		try {
			parser.nextChar(c);			
			computeEnabled();			
		} catch (Exception e) {
			errorText.setText(e.getMessage());
		}
	}
}
