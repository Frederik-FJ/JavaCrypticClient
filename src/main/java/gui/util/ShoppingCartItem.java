package gui.util;

import items.HardwareElement;

import javax.swing.*;

public class ShoppingCartItem extends JPanel {

    HardwareElement element;
    int number;
    JTextField numberField;

    public ShoppingCartItem(HardwareElement element, int number){
        this.element = element;
        this.number = number;

        init();
    }

    private void init(){
        numberField = new JTextField(2);
        numberField.setText(Integer.toString(number));
        numberField.setSize(100, numberField.getHeight());
        this.add(new JLabel(element.getType() + "-------->" + element.getName()));
        this.add(numberField);
    }

    /**
     *
     * @return returns the Number which is in the textField
     */
    public int getInputNumber(){
        try {
            return Integer.parseInt(this.numberField.getText());
        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, numberField.getText() + "---> that isn't a number");
        }
        return 0;
    }

    public HardwareElement getElement(){
        return element;
    }


}
