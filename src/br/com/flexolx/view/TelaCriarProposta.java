package br.com.flexolx.view;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.flexolx.controller.GerenciadorPropostas;
import br.com.flexolx.model.enums.FormaPagamento;
import br.com.flexolx.model.imovel.Imovel;
import br.com.flexolx.model.proposta.Proposta;
import br.com.flexolx.model.usuario.Usuario;

public class TelaCriarProposta extends JDialog {

    private JTextField txtValor;
    private JTextArea txtMensagem;
    private JComboBox<FormaPagamento> comboFormaPagamento;
    private JButton btnEnviar;

    private GerenciadorPropostas gerenciador;
    private Usuario comprador;
    private Imovel imovel;

    public TelaCriarProposta(Frame parent, GerenciadorPropostas gerenciador, Usuario comprador, Imovel imovel) {
        super(parent, "Enviar Proposta", true);
        this.gerenciador = gerenciador;
        this.comprador = comprador;
        this.imovel = imovel;

        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(450, 300);
        setLocationRelativeTo(parent);

        add(new JLabel("Valor Ofertado (R$):"));
        txtValor = new JTextField();
        add(txtValor);

        add(new JLabel("Forma de Pagamento:"));
        comboFormaPagamento = new JComboBox<>(FormaPagamento.values());
        add(comboFormaPagamento);

        add(new JLabel("Mensagem:"));
        txtMensagem = new JTextArea();
        add(new JScrollPane(txtMensagem));

        btnEnviar = new JButton("Enviar Proposta");
        add(btnEnviar);

        btnEnviar.addActionListener(e -> enviar());
    }

    private void enviar() {
        try {
            double valorDouble = Double.parseDouble(txtValor.getText());
            BigDecimal valorBd = BigDecimal.valueOf(valorDouble);
            String msg = txtMensagem.getText();
            FormaPagamento forma = (FormaPagamento) comboFormaPagamento.getSelectedItem();

            // Validade padrão configurada para 7 dias a partir de hoje
            LocalDateTime validade = LocalDateTime.now().plusDays(7);

            // Chamada ajustada ao construtor da classe Proposta
            Proposta p = new Proposta(imovel, comprador, valorBd, forma, msg, validade);
            gerenciador.enviarProposta(p);

            JOptionPane.showMessageDialog(this, "Proposta enviada com sucesso!");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor numérico válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao enviar proposta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}