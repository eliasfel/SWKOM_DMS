document.getElementById("fetchDocuments").addEventListener("click", async function() {
    try {
        const response = await fetch("http://localhost:8081/api/v1/documents");
        if (response.ok) {
            const data = await response.json();
            document.getElementById("output").innerText = JSON.stringify(data, null, 2);
        } else {
            document.getElementById("output").innerText = "Failed to fetch documents.";
        }
    } catch (error) {
        document.getElementById("output").innerText = "Error: " + error.message;
    }
});
