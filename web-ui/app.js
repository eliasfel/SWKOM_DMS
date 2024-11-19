document.getElementById("fetchDocuments").addEventListener("click", async function() {
    try {
        const response = await fetch("http://localhost:8081/documents");
        if (response.ok) {
            const text = await response.text();
            const data = { documents: text };
            document.getElementById("output").innerText = JSON.stringify(data, null, 2);
        } else {
            document.getElementById("output").innerText = "Failed to fetch documents.";
        }
    } catch (error) {
        document.getElementById("output").innerText = "Error: " + error.message;
    }
});
