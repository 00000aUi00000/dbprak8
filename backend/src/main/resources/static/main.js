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
  } else if (input.startsWith("getProductsByCategoryPath")) {
    executeGetProductsByCategoryPath(input);
  } else if (input.startsWith("getProducts")) {
    executeGetProducts(input);
  } else if (input.startsWith("finish")) {
    sendFinish();
  } else if (input.startsWith("getProduct")) {
    executeGetProduct(input);
  } else if (input.startsWith("getTopProducts")) {
    executeGetTopProducts(input);
  } else if (input.startsWith("getCategoryTree")) {
    executeGetCategoryTree();
  } else if (input.startsWith("getOffers")) {
    executeGetOffers(input);
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

function executeGetTopProducts(input) {
  const parts = input.split(" ");
  const max = parts.length > 1 ? parts[1] : "";
  fetch("/getTopProducts?max=" + encodeURIComponent(max))
    .then((res) => res.json())
    .then((data) => {
      const resultBox = document.getElementById("resultBox");
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else {
        resultBox.innerHTML = data
          ? `<table border="1" cellpadding="5" cellspacing="0">
              <thead>
                <tr>
                  <th>Top</th>
                  <th>Produkt ID</th>
                  <th>Titel</th>
                  <th>Rating</th>
                  <th>Rezensionen</th>
                  <th>Typ</th>
                </tr>
              </thead>
              <tbody>` +
            data
              .map(
                (p, index) =>
                  `<tr>` +
                  `<td class="center">${index + 1}</td>` +
                  `<td class="center">${p.produktId}</td><td>${p.titel}</td>` +
                  `<td class="center">${p.rating.toFixed(
                    2
                  )} ★</td><td class="center">${p.anzahlRezensionen}</td>` +
                  `<td class="center">${p.typ}</td>` +
                  "</tr>"
              )
              .join("") +
            `</tbody></table>`
          : "Keine Produkte verfügbar.";
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
          `<table border="1" cellpadding="5" cellspacing="0">` +
          Object.entries(data)
            .map(
              ([k, v]) =>
                `<tr><th>${capitalizeFirstLetter(k)}</th>` +
                `<td>${
                  v ? v + (k.toLocaleLowerCase() == "rating" ? "★" : "") : ""
                }</td></tr>`
            )
            .join("") +
          `</tbody></table>` +
          "<br>" +
          `<img src="${data.bild}" alt="Kein Bild verfügbar" title="${data.bild}" />`;
      }
    });
}

function executeGetProductsByCategoryPath(input) {
  input += " ";
  const path = input.substring(input.indexOf(" ") + 1).trim();
  fetch("/getProductsByCategoryPath?path=" + encodeURIComponent(path))
    .then((res) => res.json())
    .then((data) => {
      const resultBox = document.getElementById("resultBox");
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else {
        resultBox.innerHTML =
          data.length > 0
            ? `<table border="1" cellpadding="5" cellspacing="0">
              <thead>
                <tr>
                  <th></th>
                  <th>Produkt-ID</th>
                  <th>Titel</th>
                  <th>Typ</th>
                </tr>
              </thead>
              <tbody>` +
              data
                .map(
                  (it, index) =>
                    `<tr>` +
                    `<td>${index + 1}</td>` +
                    `<td>${it.produktId}</td>` +
                    `<td>${it.titel}</td>` +
                    `<td class="center">${it.typ}</td>` +
                    "</tr>"
                )
                .join("") +
              `</tbody></table>`
            : `<p>Keine Produkte in \'${path}\' vorhanden.</p>`;
      }
    });
}

function executeGetCategoryTree() {
  fetch("/getCategoryTree")
    .then((res) => res.json())
    .then((data) => {
      const resultBox = document.getElementById("resultBox");
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else {
        let tree = `<ul id="tree">` + renderTree(data) + "</ul>";
        resultBox.innerHTML = tree;
      }
    });
}

function executeGetOffers(input) {
  const parts = input.split(" ");
  const id = parts.length > 1 ? parts[1] : "";
  fetch("/getOffers?id=" + encodeURIComponent(id))
    .then((res) => res.json())
    .then((data) => {
      const resultBox = document.getElementById("resultBox");
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else {
        resultBox.innerHTML =
          data.length > 0
            ? `<table border="1" cellpadding="5" cellspacing="0">
              <thead>
                <tr>
                  <th>Filiale</th>
                  <th>Angebot</th>
                </tr>
              </thead>
              <tbody>` +
              data
                .sort((it) => it.filiale.name)
                .map(
                  (o) =>
                    `<tr>` +
                    `<td class="center">${o.filiale.name} - ${o.filiale.anschrift}</td>` +
                    `<td> [${
                      o.details.angebotId
                    }]: ${o.details.preis.toLocaleString("de-DE", {
                      style: "currency",
                      currency: "EUR",
                    })} (${o.details.zustand})</td>` +
                    "</tr>"
                )
                .join("") +
              `</tbody></table>`
            : `<p>Keine Angebote für ${id} verfügbar.</p>`;
      }
    });
}

function capitalizeFirstLetter(val) {
  return String(val).charAt(0).toUpperCase() + String(val).slice(1);
}

function renderTree(nodes) {
  if (!nodes || nodes.length === 0) return "";
  let html = "";
  for (const node of nodes) {
    html += `<li>`;
    html +=
      node.childs && node.childs.length > 0
        ? `<span class="caret">${node.name}</span>`
        : `${node.name}`;
    if (node.childs && node.childs.length > 0) {
      html += `<ul class="nested">`;
      html += renderTree(node.childs);
      html += "</ul>";
    }
    html += "</li>";
  }
  return html;
}

document.addEventListener("DOMContentLoaded", () => {
  const resultBox = document.getElementById("resultBox");
  resultBox.addEventListener("click", function (event) {
    if (event.target.classList.contains("caret")) {
      const nested = event.target.parentElement.querySelector(".nested");
      if (nested) nested.classList.toggle("active");
      event.target.classList.toggle("caret-down");
    }
  });
});