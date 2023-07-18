package pdv.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import pdv.dominio.*;
import pdv.dominio.excecoes.DescricaoProdutoInexistente;
import pdv.dominio.pagamento.Operadora;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class pdvGUIController {

    @FXML
    private ComboBox<String> regComboBox;
    @FXML
    private ComboBox<String> itemsComboBox;
    @FXML
    private ComboBox<String> paymentComboBox;
    @FXML
    private ComboBox<Operadora> issuerComboBox;
    @FXML
    private Spinner<Integer> qtdSpinner, qtdParcelas;
    @FXML
    private Button itemAddButton, endSaleButton, payButton;
    @FXML
    private Button newSaleButton;
    @FXML
    private Button confirmValueButton;
    @FXML
    private Label totalLabel,changeLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label selectProduct;
    @FXML
    private Label instLabel;
    @FXML
    private Label issuerLabel;
    @FXML
    private Label parcLabel;
    @FXML
    private Label qtdProdutosLabel;
    @FXML
    private Label quantiaLabel;

    @FXML
    private TextField instTextField;

    @FXML
    private TextField qtdFornecidaTextField;

    @FXML
    private AnchorPane headerPane;
    @FXML
    private AnchorPane salePane;
    @FXML
    private AnchorPane paymentPane;


    @FXML
    private void initialize(){
        Locale brazil = Locale.of("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(brazil);


        Endereco endereco = new Endereco("Rua X", "", 5,
                "Alfenas", "Aeroporto", "MG", "37130-000");
        Loja loja = new Loja("Supermercado Preço Bão", endereco);

        regComboBox.getItems().addAll("R01", "R02", "R03");
        regComboBox.setValue("R01");

        itemsComboBox.getItems().addAll("01", "02", "03", "04", "05","06","07","08","09","10");
        paymentComboBox.getItems().addAll("Dinheiro", "Crédito", "Cheque");
        issuerComboBox.setItems(FXCollections.observableArrayList(Operadora.values()));


        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000);
        qtdSpinner.setValueFactory(valueFactory);

        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3);
        qtdParcelas.setValueFactory(valueFactory2);

        paymentPane.setDisable(true);
        salePane.setDisable(true);
        payButton.setDisable(true);

        issuerComboBox.setVisible(false);
        issuerLabel.setVisible(false);
        qtdParcelas.setVisible(false);
        parcLabel.setVisible(false);
        instTextField.setVisible(false);
        instLabel.setVisible(false);
        changeLabel.setVisible(false);
        selectProduct.setVisible(false);
        quantiaLabel.setVisible(false);
        qtdFornecidaTextField.setVisible(false);
        confirmValueButton.setVisible(false);


        String reg = regComboBox.getValue();
        AtomicReference<Registradora> registradora = new AtomicReference<>(loja.getRegistradora(reg));

        //Seleciona a registradora
        regComboBox.setOnAction(actionEvent -> {
            String regist = regComboBox.getSelectionModel().getSelectedItem();
            registradora.set(loja.getRegistradora(regist));
        });

        //Inicia uma venda com a registradora selecionada
        newSaleButton.setOnAction(actionEvent -> {
            registradora.get().criarNovaVenda();
            headerPane.setDisable(true);
            salePane.setDisable(false);

            //reseta as informacoes de tela
            selectProduct.setText(null);
            qtdProdutosLabel.setText("qtd. produtos");
            subtotalLabel.setText("subtotal:");
            totalLabel.setText("Total: R$ 0,00");
            qtdSpinner.getValueFactory().setValue(0);
            paymentPane.setDisable(true);
            instTextField.setText("");
            qtdParcelas.getValueFactory().setValue(1);
            qtdFornecidaTextField.setText("");
            changeLabel.setText("Troco: R$ 0,00");
        });


        //Adiciona um item a compra
        itemAddButton.setOnAction(actionEvent -> {
            String item = itemsComboBox.getValue();
            Integer value = qtdSpinner.getValue();
            try {
                registradora.get().entrarItem(item,value);
                qtdProdutosLabel.setText("qtd. produtos: " + registradora.get().getVendaCorrente().getItemQuantity());
                subtotalLabel.setText("subtotal: " + formatter.format(registradora.get().getVendaCorrente().calcularTotalVenda()));
                qtdSpinner.getValueFactory().setValue(0);
            } catch (DescricaoProdutoInexistente e) {
                throw new RuntimeException(e);
            }

        });
        
        //finaliza a venda atual
        endSaleButton.setOnAction(actionEvent -> {
            registradora.get().finalizarVenda();
            totalLabel.setText("Total: " + formatter.format(registradora.get().getVendaCorrente().calcularTotalVenda()));
            paymentPane.setDisable(false);
            salePane.setDisable(true);
            headerPane.setDisable(false);
        });


        //permite a exibição do produto que está selecionado
        itemsComboBox.setOnAction(actionEvent -> {
            selectProduct.setVisible(true);
            String id = itemsComboBox.getSelectionModel().getSelectedItem();
            try {
                selectProduct.setText(registradora.get().getCatalogo().getDescricaoProduto(id).toString());
            } catch (DescricaoProdutoInexistente e) {
                throw new RuntimeException(e);
            }
        });

        //Altera a exibição dos campos para determinado tipo de pagamento escolhido
        paymentComboBox.setOnAction(actionEvent -> {
            String selected = paymentComboBox.getSelectionModel().getSelectedItem();
            switch (selected) {
                case "Dinheiro" ->{
                    issuerComboBox.setVisible(false);
                    issuerLabel.setVisible(false);
                    qtdParcelas.setVisible(false);
                    parcLabel.setVisible(false);
                    instTextField.setVisible(false);
                    instLabel.setVisible(false);
                    changeLabel.setVisible(true);
                    qtdFornecidaTextField.setVisible(true);
                    quantiaLabel.setVisible(true);
                    confirmValueButton.setVisible(true);

                    payButton.setDisable(true);

                }
                case "Crédito" -> {
                    payButton.setDisable(true);

                    issuerComboBox.setVisible(true);
                    issuerLabel.setVisible(true);
                    qtdParcelas.setVisible(true);
                    parcLabel.setVisible(true);
                    instTextField.setVisible(false);
                    instLabel.setVisible(false);
                    changeLabel.setVisible(false);
                    qtdFornecidaTextField.setVisible(false);
                    confirmValueButton.setVisible(false);
                    quantiaLabel.setVisible(false);

                }
                case "Cheque"-> {
                    payButton.setDisable(true);

                    issuerComboBox.setVisible(false);
                    issuerLabel.setVisible(false);
                    qtdParcelas.setVisible(false);
                    parcLabel.setVisible(false);
                    instTextField.setVisible(true);
                    instLabel.setVisible(true);
                    changeLabel.setVisible(false);
                    qtdFornecidaTextField.setVisible(false);
                    confirmValueButton.setVisible(false);
                    quantiaLabel.setVisible(false);
                }

            }
        });
        //permite o pagamento somente se uma opcao for selecionada
        issuerComboBox.setOnAction(actionEvent -> {
            payButton.setDisable(false);
        });

        qtdFornecidaTextField.setPromptText("Valor");

        //event listener para verificar se a entrada corrente eh valida para efetuar o pagamento
        confirmValueButton.setDisable(true);
        qtdFornecidaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // checa se a entrada eh valida
            confirmValueButton.setDisable(!isValidDouble(newValue));
        });

        //botao para confirmar a quantia fornecida(caso pagamento por dinheiro), se a quantia for insuficiente exibe um erro
        confirmValueButton.setOnAction(actionEvent -> {
            double quantia = Double.parseDouble(qtdFornecidaTextField.getText());
            double totalVenda = registradora.get().getVendaCorrente().calcularTotalVenda();
            if (quantia < totalVenda){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Quantia insuficiente");
                alert.setContentText("A quantia fornecida não é suficiente para realizar a compra!");
                alert.showAndWait();
                payButton.setDisable(true);
            } else {
                payButton.setDisable(false);
                changeLabel.setText("Troco: " + formatter.format(quantia - totalVenda));
            }

        });
        //Em caso de pagamento por cheque, permite o pagamento apenas se o campo instituicao for preenchido
        instTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            payButton.setDisable(instTextField.getText().equals(""));
        });

        //recolhe a informacao dos campos e efetua o pagamento
        payButton.setOnAction((actionEvent -> {
            double troco = 0.0;
            double totalVenda = registradora.get().getVendaCorrente().calcularTotalVenda();
            String paymentType = paymentComboBox.getValue();
            int parcelas = qtdParcelas.getValue();
            switch (paymentType) {
                case "Dinheiro" -> {
                    registradora.get().fazerPagamento(totalVenda);
                    double quantia = Double.parseDouble(qtdFornecidaTextField.getText());
                    troco =  quantia - totalVenda;

                }
                case "Crédito" -> {
                    Operadora issuer = issuerComboBox.getValue();
                    registradora.get().fazerPagamento(totalVenda, issuer, parcelas, TipoCalculadora.JUROS_SIMPLES);
                }
                case "Cheque"-> {
                    String inst = instTextField.getText();
                    System.out.println(inst);
                    registradora.get().fazerPagamento(totalVenda, inst);
                }

            }

            //reseta as informacoes para a proxima venda
            selectProduct.setText(null);
            qtdProdutosLabel.setText("qtd. produtos");
            subtotalLabel.setText("subtotal:");
            totalLabel.setText("Total: R$ 0,00");
            qtdSpinner.getValueFactory().setValue(0);
            paymentPane.setDisable(true);
            instTextField.setText("");
            qtdParcelas.getValueFactory().setValue(1);
            qtdFornecidaTextField.setText("");
            changeLabel.setText("Troco: R$ 0,00");

            gerarRecibo(registradora.get(), troco);
        }));



    }


    public void gerarRecibo(Registradora registradora, double troco) {
        Venda venda = registradora.getVendaCorrente();
        System.out.println("");
        System.out.println("--------------------------- Supermercado Preço Bão ---------------------------");
        System.out.println("                             Registradora : " + registradora.getId());
        System.out.println("\t\t\t\tCUPOM FISCAL");
        System.out.println(venda);
        System.out.println("Troco................: R$ " + troco);
    }

    private boolean isValidDouble(String input) {
        try {
            double value = Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
