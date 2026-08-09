package br.com.flexolx.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;

import br.com.flexolx.controller.GerenciadorPropostas;
import br.com.flexolx.model.proposta.Proposta;
import br.com.flexolx.model.usuario.Usuario;

public class TelaMinhasPropostas extends JFrame {

    private GerenciadorPropostas gerenciador;
    private Usuario usuarioLogado;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JButton btnAceitar;
    private JButton btnRecusar;

    public TelaMinhasPropostas(GerenciadorPropostas gerenciador, Usuario usuarioLogado) {
        super("Minhas Propostas Recebidas");
        this.gerenciador = gerenciador;
        this.usuarioLogado = usuarioLogado;

        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        carregarPropostas();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Comprador", "Imóvel", "Valor (R$)", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Desabilita edição direta nas células
            }
        };

        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        btnAceitar = new JButton("Aceitar Proposta");
        btnRecusar = new JButton("Recusar Proposta");

        painelBotoes.add(btnAceitar);
        painelBotoes.add(btnRecusar);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAceitar.addActionListener(e -> responderProposta(true));
        btnRecusar.addActionListener(e -> responderProposta(false));
    }

    private void carregarPropostas() {
        tableModel.setRowCount(0);
        List<Proposta> recebidas = gerenciador.listarPropostasRecebidas(usuarioLogado);
        
        if (recebidas != null) {
            for (Proposta p : recebidas) {
                tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getProponente().getNome(),
                    p.getImovel().getTitulo(),
                    p.getValorOfertado(),
                    p.getStatus()
                });
            }
        }
    }

    private void responderProposta(boolean aceitar) {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma proposta na tabela.");
            return;
        }

        UUID id = (UUID) tableModel.getValueAt(linhaSelecionada, 0);

        try {
            if (aceitar) {
                gerenciador.aceitarProposta(id, usuarioLogado);
                JOptionPane.showMessageDialog(this, "Proposta aceita com sucesso!");
            } else {
                String motivo = JOptionPane.showInputDialog(this, "Informe o motivo da recusa:");
                if (motivo != null && !motivo.trim().isEmpty()) {
                    gerenciador.recusarProposta(id, usuarioLogado, motivo);
                    JOptionPane.showMessageDialog(this, "Proposta recusada!");
                }
            }
            carregarPropostas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}