<?php
// Ativar exibição de erros (útil para depuração; retire em produção)
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Opcional: registre o conteúdo recebido para depuração
file_put_contents('php://stderr', print_r($_POST, true));

$host = "localhost";
$user = "root";
$password = "";
$database = "projetows";

// Conectar ao banco de dados
$conn = new mysqli($host, $user, $password, $database);
if ($conn->connect_error) {
    die("Erro na conexão: " . $conn->connect_error);
}

// ID fixo para garantir que apenas os dados de pessoa_id = 1 sejam armazenados
$pessoa_id_permitido = 28;

// Verifica se os dados foram recebidos via POST
if (isset($_POST['pessoa_id'], $_POST['heartRate'], $_POST['spO2'])) {
    // Converte os valores para inteiros (note que as casas decimais serão truncadas)
    $pessoa_id = intval($_POST['pessoa_id']);
    $heartRate = floatval($_POST['heartRate']);
    $spO2 = floatval($_POST['spO2']);

    // Formata o instante exatamente como desejado, por exemplo: 21:58:54 em 02/02/2025
    $instante = date("H:i:s \\e\\m d/m/Y");

    // Verifica se o ID enviado é permitido
    if ($pessoa_id !== $pessoa_id_permitido) {
        echo "Erro: ID não autorizado!";
        exit();
    }

    // Verifica se o ID 28 existe na tabela Pessoa
    $stmt_check = $conn->prepare("SELECT id FROM Pessoa WHERE id = ?");
    $stmt_check->bind_param("i", $pessoa_id);
    $stmt_check->execute();
    $result = $stmt_check->get_result();
    
    if ($result->num_rows === 0) {
        echo "Erro: Pessoa com ID 28 não encontrada!";
        exit();
    }
    $stmt_check->close();

    // Insere na tabela FreqCardiaca
    $stmt1 = $conn->prepare("INSERT INTO FreqCardiaca (pessoa_id, num, instante) VALUES (?, ?, ?)");
    $stmt1->bind_param("iis", $pessoa_id, $heartRate, $instante);
    
    // Insere na tabela SatOx
    $stmt2 = $conn->prepare("INSERT INTO SatOx (pessoa_id, valor, instante) VALUES (?, ?, ?)");
    $stmt2->bind_param("iis", $pessoa_id, $spO2, $instante);

    if ($stmt1->execute() && $stmt2->execute()) {
        echo "Dados armazenados com sucesso!";
    } else {
        echo "Erro ao armazenar os dados!";
    }

    // Fecha as conexões preparadas
    $stmt1->close();
    $stmt2->close();
} else {
    echo "Erro: Dados incompletos!";
}

// Fecha a conexão com o banco de dados
$conn->close();
?>