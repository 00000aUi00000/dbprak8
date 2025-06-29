function insertCommand(command) {
  document.getElementById("commandInput").value = command;
}

function sendInit() {
  fetch("/init", { method: "POST" })
    .then((res) => res.text())
    .then((txt) => alert(txt));
}

function sendFinish() {
  fetch("/finish", { method: "POST" })
    .then((res) => res.text())
    .then((txt) => alert(txt));
}

function executeCommand() {
  const input = document.getElementById("commandInput").value.trim();
  if (input.startsWith("init")) {
    sendInit();
  } else if (input.startsWith("getProducts")) {
    executeGetProducts(input);
  } else if (input.startsWith("finish")) {
    sendFinish();
  } else if (input.startsWith("getProduct")) {
    executeGetProduct(input);
  } else {
    alert("Unbekannter Befehl: " + input);
  }
}

function executeGetProducts(input) {
  const parts = input.split(" ");
  const pattern = parts.length > 1 ? parts.slice(1).join(" ") : "";
  fetch("/getProducts?pattern=" + encodeURIComponent(pattern))
    .then((res) => res.json())
    .then((data) => {
      const resultBox = document.getElementById("resultBox");
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else if (data.length === 0) {
        resultBox.innerText = "Keine Produkte gefunden.";
      } else {
        resultBox.innerHTML =
          "<ul>" +
          data
            .map(
              (p) =>
                //Was wird in welcher Reihenfolge angezeigt
                `<li><strong>${p.typ}</strong>: [${p.produktId}] ${p.titel}</li>`
            )
            .join("") +
          "</ul>";
      }
    });
}

function executeGetProduct(input) {
  const parts = input.split(" ");
  const id = parts.length > 1 ? parts[1] : "";
  fetch("/getProduct?id=" + encodeURIComponent(id))
    .then((res) => res.json())
    .then((data) => {
      const resultBox = document.getElementById("resultBox");
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else {
        resultBox.innerHTML =
          `<h3>${data.typ}</h3>` +
          "<hr>" +
          Object.entries(data)
            .filter(([k, v]) => k.toLocaleLowerCase() != "typ")
            .map(([k, v], index) => `<p><b>${k}</b>: ${v ? v : "-"}</p>`)
            .join("") +
          "<br>" +
          `<img src="${data.bild}" alt="Kein Bild verfügbar" title="${data.bild}" />`;
      }
    });
}
