import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class Frame extends JFrame {
    JFrame frame = new JFrame("Операции над онтологиями");
    JPanel panel = new JPanel();

    JButton fileChooser = new JButton("...");
    JButton loadFile = new JButton("загрузить");
    JButton deleteButton = new JButton("удалить");
    JButton saveButton = new JButton("сохранить");
    JButton binaryOperations = new JButton("бинарные операции");
    JButton unaryOperations = new JButton("унарные операции");

    private JComboBox graphList = new JComboBox();

    private JTextArea report = new JTextArea();

    private JLabel fileChooserLabel = new JLabel("файл не выбран");

    private File file = null;
    private String fileName = "";
    private ArrayList<String> label_List = new ArrayList<String>();

    Frame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.setLayout(null);
        panel.setVisible(true);
        panel.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        frame.add(panel);

        fileChooserLabel.setBounds(10, 0, 150, 20);
        fileChooserLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(fileChooserLabel);

        fileChooser.setBounds(160, 0, 20, 20);
        panel.add(fileChooser);
        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = fileopen.getSelectedFile();
                    fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                    fileChooserLabel.setText(file.getName());
                    loadFile.setEnabled(true);
                }
            }
        });

        JLabel delimiterLabel = new JLabel("Разделитель:");
        delimiterLabel.setBounds(10,fileChooserLabel.getY() + 25, 100, 20);
        panel.add(delimiterLabel);

        JTextField delimiterText = new JTextField();
        delimiterText.setBounds(delimiterLabel.getX() + 105, fileChooserLabel.getY() + 25, 20, 20);
        panel.add(delimiterText);

        loadFile.setBounds(10, delimiterLabel.getY() + 25, 100, 20);
        loadFile.setEnabled(false);
        panel.add(loadFile);
        loadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Connector.getLabels().contains(fileName))
                    report.append("Файл " + fileName + " уже существует!\n");
                else {
                    Connector.loadTXT(file, delimiterText.getText());
                    label_List.add(fileName);
                    graphList.addItem(fileName);
                    report.append("Файл " + fileName + " загружен\n");
                }
            }
        });

        label_List = Connector.getLabels();
        String[] items = new String[label_List.size()];
        int i = 0;
        for (String s : label_List) {
            items[i] = s;
            i++;
        }

        graphList = new JComboBox(items);
        graphList.setBounds(10, loadFile.getY() + 45, 150, 20);
        panel.add(graphList);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        };
        graphList.addActionListener(actionListener);

        deleteButton.setBounds(10, graphList.getY() + 25, 100, 20);
        panel.add(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String delFile = graphList.getSelectedItem().toString();
                Connector.deleteGraph(delFile);
                label_List.remove(graphList.getSelectedItem());
                graphList.removeItem(graphList.getSelectedItem());
                report.append("Файл " + delFile + " удален\n");
            }
        });

        saveButton.setBounds(10, deleteButton.getY() + 25, 100, 20);
        panel.add(saveButton);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String saveFile = graphList.getSelectedItem().toString();

                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Сохранить файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = fileopen.getSelectedFile();
                    fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                    loadFile.setEnabled(true);
                    if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("xml")) {
                        FileWorker.write_RDF(Connector.getOntology(saveFile), file);
                        report.append("Файл " + saveFile + ".xml" + " сохранен\n");
                    }
                    if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("txt")) {
                        FileWorker.write_TXT(Connector.getOntology(saveFile), file);
                        report.append("Файл " + saveFile + ".txt" + " сохранен\n");
                    }


                }
            }
        });

        binaryOperations.setBounds(10, saveButton.getY() + 45, 160, 20);
        panel.add(binaryOperations);
        binaryOperations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                binaryFrame();
            }
        });

        unaryOperations.setBounds(10, binaryOperations.getY() + 25, 160, 20);
        panel.add(unaryOperations);
        unaryOperations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unaryFrame();
            }
        });

        report.setBounds(185, 1, 190, 200);
        report.setLineWrap(true);
        report.setWrapStyleWord(true);
        panel.add(report);
        //panel.add(new JScrollPane(report));


        panel.revalidate();
        panel.repaint();

    }


    public void binaryFrame() {

        JFrame bFrame = new JFrame("Бинарные операции над онтологиями");
        bFrame.setSize(500, 300);
        bFrame.setLocationRelativeTo(null);
        bFrame.setVisible(true);

        JPanel bPanel = new JPanel();
        bPanel.setLayout(null);
        bPanel.setVisible(true);
        bPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        bFrame.add(bPanel);

        JLabel label1 = new JLabel("выберите онтологии и тезаурус");
        label1.setBounds(10, 0, 300, 20);
        bPanel.add(label1);

        String[] items = new String[label_List.size()];
        int i = 0;
        for (String s : label_List) {
            items[i] = s;
            i++;
        }

        JTextArea report2 = new JTextArea();

        JComboBox graphListA = new JComboBox(items);
        graphListA.setBounds(10, label1.getY() + 25, 150, 20);
        bPanel.add(graphListA);

        JComboBox graphListB = new JComboBox(items);
        graphListB.setBounds(graphListA.getX() + 155, label1.getY() + 25, 150, 20);
        bPanel.add(graphListB);

        JComboBox graphListT = new JComboBox(items);
        graphListT.setBounds(graphListB.getX() + 155, label1.getY() + 25, 150, 20);
        bPanel.add(graphListT);

        ArrayList<JComboBox> comboBoxesList = new ArrayList<JComboBox>();
        comboBoxesList.add(graphListA);
        comboBoxesList.add(graphListB);
        comboBoxesList.add(graphListT);

        JLabel label2 = new JLabel("название выходного графа:");
        label2.setBounds(10, graphListA.getY() + 30, 180, 20);
        bPanel.add(label2);

        JTextField newOntoLabel = new JTextField();
        newOntoLabel.setBounds(label2.getX() + 185, graphListA.getY() + 30, 100, 20);
        bPanel.add(newOntoLabel);

        JButton intersection = new JButton("пересечение");
        JButton union = new JButton("объединение ");

        intersection.setBounds(10, label2.getY() + 30, 150, 20);
        bPanel.add(intersection);
        intersection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newLabel = newOntoLabel.getText();
                Ontology a = new Ontology(graphListA.getSelectedItem().toString());
                Ontology b = new Ontology(graphListB.getSelectedItem().toString());
                Operations.intersection_Ontologies(a, b, graphListT.getSelectedItem().toString(), newLabel, true);
                report2.append("Пересечение: " + a.getOntoName() + " и " + b.getOntoName() + "\n");
                refreshComboBox(comboBoxesList, newLabel);
                bFrame.repaint();
            }
        });
        union.setBounds(10, intersection.getY() + 25, 150, 20);
        bPanel.add(union);
        union.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newLabel = newOntoLabel.getText();
                Ontology a = new Ontology(graphListA.getSelectedItem().toString());
                Ontology b = new Ontology(graphListB.getSelectedItem().toString());
                Operations.union_Ontologies(a, b, graphListT.getSelectedItem().toString(), newLabel);
                report2.append("Объединение: " + a.getOntoName() + " и " + b.getOntoName() + "\n");
                refreshComboBox(comboBoxesList, newLabel);
                bFrame.repaint();
            }
        });

        report2.setBounds(intersection.getX() + 160, intersection.getY(), 200, 200);
        bPanel.add(report2);

        bPanel.revalidate();
        bPanel.repaint();
    }

    public void unaryFrame() {
        JFrame uFrame = new JFrame("Унарные операции над онтологиями");
        uFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JRadioButton definition = new JRadioButton("По понятиям", false);
        JRadioButton relation = new JRadioButton("По отношениям", true);

        uFrame.setSize(500, 400);
        uFrame.setLocationRelativeTo(null);
        uFrame.setVisible(true);

        JPanel uPanel = new JPanel();
        uPanel.setLayout(null);
        uPanel.setVisible(true);
        uPanel.setBounds(0, 0, uFrame.getWidth(), uFrame.getHeight());

        uFrame.add(uPanel);

        JTextArea report3 = new JTextArea();

        JLabel label1 = new JLabel("выберите онтологию и тезаурус");
        label1.setBounds(10, 0, 300, 20);
        uPanel.add(label1);

        String[] items = new String[label_List.size()];
        int i = 0;
        for (String s : label_List) {
            items[i] = s;
            i++;
        }
        JComboBox graphListA = new JComboBox(items);
        graphListA.setBounds(10, label1.getY() + 25, 150, 20);
        uPanel.add(graphListA);


        JComboBox graphListT = new JComboBox(items);
        graphListT.setBounds(graphListA.getX() + 155, label1.getY() + 25, 150, 20);
        uPanel.add(graphListT);

        ArrayList<JComboBox> comboBoxesList = new ArrayList<JComboBox>();
        comboBoxesList.add(graphListA);
        comboBoxesList.add(graphListT);

        JLabel label2 = new JLabel("название выходного графа:");
        label2.setBounds(10, graphListA.getY() + 30, 180, 20);
        uPanel.add(label2);

        JTextField newOntoLabel = new JTextField();
        newOntoLabel.setBounds(label2.getX() + 185, graphListA.getY() + 30, 100, 20);
        uPanel.add(newOntoLabel);

        JButton projecting = new JButton("аспектное проецирование ");
        JButton scaling = new JButton("масштабирование ");

        projecting.setBounds(10, label2.getY() + 40, 200, 20); //кнопка аспектного проецирования
        uPanel.add(projecting);

        projecting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame f_Projecting = new JFrame("Аспектное проецирование");
                f_Projecting.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                f_Projecting.setSize(400, 300);
                f_Projecting.setVisible(true);

                JPanel p_Projecting = new JPanel();
                p_Projecting.setLayout(null);
                p_Projecting.setVisible(true);
                p_Projecting.setBounds(0, 0, f_Projecting.getWidth(), f_Projecting.getHeight());

                f_Projecting.add(p_Projecting);

                JLabel l_Projecting = new JLabel();
                l_Projecting.setBounds(10, 0, 300, 20);
                p_Projecting.add(l_Projecting);

                String onto_Label = graphListA.getSelectedItem().toString();
                String tes_Label = graphListT.getSelectedItem().toString();
                ArrayList<String> item_List;
                String[] items;
                if (definition.isSelected()) {
                    l_Projecting.setText("Выберите понятия");
                    item_List = Connector.getDefinitions(onto_Label);
                    items = new String[item_List.size()];
                } else {
                    l_Projecting.setText("Выберите отношения");
                    item_List = Connector.getRelations(onto_Label);
                    items = new String[item_List.size()];

                }
                int i = 0;
                for (String s : item_List) {
                    items[i] = s;
                    i++;
                }

                JComboBox list_Projecting = new JComboBox(items);
                list_Projecting.setBounds(10, l_Projecting.getY() + 25, 150, 20);
                p_Projecting.add(list_Projecting);

                DefaultTableModel model = new DefaultTableModel(); //Создаем таблицу;
                model.addColumn("Выбранные элементы");
                JTable table_Items = new JTable(model);

                table_Items.setBounds(list_Projecting.getX() + 170, list_Projecting.getY(), 150, 0);
                p_Projecting.add(table_Items);

                list_Projecting.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        model.addRow(new String[]{list_Projecting.getSelectedItem().toString()});
                        table_Items.setSize(150, table_Items.getHeight() + table_Items.getRowHeight());
                    }
                });

                p_Projecting.revalidate();
                p_Projecting.repaint();
            }
        });

        ButtonGroup group_P = new ButtonGroup();
        definition.setBounds(10, projecting.getY() + 25, 150, 20);
        uPanel.add(definition);
        group_P.add(definition);

        relation.setBounds(10, projecting.getY() + 45, 150, 20);
        uPanel.add(relation);
        group_P.add(relation);

        scaling.setBounds(10, relation.getY() + 45, 200, 20); //кнопка масштабирования
        uPanel.add(scaling);

        ButtonGroup group_M = new ButtonGroup();
        JRadioButton decompositionButton = new JRadioButton("Декомпозиция", false);
        decompositionButton.setBounds(10, scaling.getY() + 25, 150, 20);
        uPanel.add(decompositionButton);
        group_M.add(decompositionButton);

        JRadioButton contractionButton = new JRadioButton("Укрупнение", true);
        contractionButton.setBounds(10, scaling.getY() + 45, 150, 20);
        uPanel.add(contractionButton);
        group_M.add(contractionButton);

        report3.setBounds(projecting.getX() + 220, projecting.getY(), 200, 200);
        uPanel.add(report3);


        uPanel.revalidate();
        uPanel.repaint();
    }

    private void refreshComboBox(ArrayList<JComboBox> a, String label) {
        a.add(graphList);
        for (JComboBox b : a) {
            b.addItem(label);
            label_List.add(label);
        }
        frame.repaint();
    }
}