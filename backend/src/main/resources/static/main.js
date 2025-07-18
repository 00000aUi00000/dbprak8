function insertCommand(command) {
  document.getElementById("commandInput").value = command;
}

function sendInit() {
  fetch("/init", { method: "POST" })
    .then((res) => res.text())
    .then((txt) => {
      alert(txt);
      updateDbStatus(); 
    });
}

function sendFinish() {
  fetch("/finish", { method: "POST" })
    .then((res) => res.text())
    .then((txt) => {
      alert(txt);
      updateDbStatus(); 
    });
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
  } else if (input.startsWith("getTrolls")) {
    executeGetTrolls(input);
  } else if (input.startsWith("getSimilarCheaperProduct")) {
  executeGetSimilarCheaperProduct(input);
  } else if (input.startsWith("getRezensionen")) {
  executeGetRezensionen(input);
  } else if (input.startsWith("addNewReview")) {
  executeAddNewReview(input);
  } else {
    alert("Unbekannter Befehl: " + input);
  }
}

String.prototype.replaceAt = function(index, replacement) {
  return this.substring(0, index) + replacement + this.substring(index + replacement.length);
}

function displayLoading() {
  const loading = document.getElementById("loading");
  const max = 10;

  loading.innerHTML = "･".repeat(max);

  var i = 0;

  intervalId = window.setInterval(function () {
    if (i == max) {
      i = 0;
      loading.innerHTML = loading.innerHTML.replaceAll("￭", "･");
    } else {
      loading.innerHTML = loading.innerHTML.replaceAt(i, "￭");
      i++;
    }
  }, 500);

  return intervalId;
}

function stopLoading(intervalId) {
  var loading = document.getElementById("loading");

  loading.innerHTML = "";
  clearInterval(intervalId);
}

function updateDbStatus() {
  fetch('/status/db')
    .then(response => {
      const el = document.getElementById('db-status');
      if (response.ok) {
        el.textContent = '✅ Verbindung aktiv';
        el.style.color = 'green';
      } else {
        el.textContent = '❌ Nicht verbunden';
        el.style.color = 'red';
      }
    })
    .catch(() => {
      const el = document.getElementById('db-status');
      el.textContent = '❌ Fehler beim Prüfen';
      el.style.color = 'red';
    });
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
      } else {
        resultBox.innerHTML = data && data.length > 0
          ? `<table border="1" cellpadding="5" cellspacing="0">
              <thead>
                <tr>
                  <th>Typ</th>
                  <th>Produkt ID</th>
                  <th>Titel</th>
                  <th>Rating</th>
                  <th>Verkaufsrang</th>
                  <th>Erscheinungsdatum</th>
                </tr>
              </thead>
              <tbody>` +
            data
              .map(
                (p) =>
                  `<tr>` +
                  `<td class="center">${p.typ}</td>` +
                  `<td class="center">${p.produktId}</td>` +
                  `<td>${p.titel}</td>` +
                  `<td class="center">${p.rating != null ? p.rating.toFixed(2) + " ★" : "-"}</td>` +
                  `<td class="center">${p.verkaufsrang ?? "-"}</td>` +
                  `<td class="center">${p.erscheinungsdatum ?? "-"}</td>` +
                  "</tr>"
              )
              .join("") +
            `</tbody></table>`
          : "Keine Produkte gefunden.";
      }
    });
}

function executeGetTopProducts(input) {
  const parts = input.trim().split(/\s+/); // trennt nach Leerzeichen
  const max = parts.length > 1 ? parts[1] : "";
  const typ = parts.length > 2 ? parts[2] : "";

  const url = `/getTopProducts?max=${encodeURIComponent(max)}&typ=${encodeURIComponent(typ)}`;

  fetch(url)
    .then((res) => res.json())
    .then((data) => {
      const resultBox = document.getElementById("resultBox");
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else {
        resultBox.innerHTML = data && data.length > 0
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
                  `<td class="center">${p.rating.toFixed(2)} ★</td>` +
                  `<td class="center">${p.anzahlRezensionen}</td>` +
                  `<td class="center">${p.typ ?? ""}</td>` +
                  `</tr>`
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
  const intervalId = displayLoading();
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
    })
    .finally(() => stopLoading(intervalId));
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
                    <th>Angebots-ID</th>
                    <th>Preis</th>
                    <th>Zustand</th>
                  </tr>
                </thead>
                <tbody>` +
              data
                .sort((it) => it.filiale.name)
                .map(
                  (o) =>
                    `<tr>
                      <td class="center">${o.filiale.name} - ${o.filiale.anschrift}</td>
                      <td>[${o.details.angebotId}]</td>
                      <td>${o.details.preis.toLocaleString("de-DE", {
                        style: "currency",
                        currency: "EUR",
                      })}</td>
                      <td>${o.details.zustand}</td>
                    </tr>`
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

function executeGetTrolls(input) {
  const parts = input.split(" ");
  const rawRating = parts[1];
  const order = (parts[2] || "").toLowerCase(); // optional

  const normalizedRating = rawRating?.replace(",", ".");
  const rating = parseFloat(normalizedRating);
  const asc = order === "asc";

  const resultBox = document.getElementById("resultBox");

  if (isNaN(rating)) {
    resultBox.innerText = "❌ Bitte eine gültige Zahl als Rating eingeben. Beispiel: getTrolls 3.0 asc";
    return;
  }

  fetch(`/getTrolls?maxRating=${encodeURIComponent(rating)}&asc=${asc}`)
    .then((res) => res.json())
    .then((data) => {
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else {
        resultBox.innerHTML =
          data.length > 0
            ? `<table border="1" cellpadding="5" cellspacing="0">
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Durchschnittsbewertung</th>
                  </tr>
                </thead>
                <tbody>` +
              data
                .map(
                  (user) => `
                    <tr>
                      <td>${user.username}</td>
                      <td>${user.avgPunkte.toFixed(2)}</td>
                    </tr>`
                )
                .join("") +
              `</tbody></table>`
            : `<p>Keine Nutzer mit einer Durchschnittsbewertung unter ${rating.toLocaleString("de-DE")} gefunden.</p>`;
      }
    })
    .catch((error) => {
      resultBox.innerText = "❌ Fehler beim Abrufen der Daten: " + error.message;
    });
}

function executeGetSimilarCheaperProduct(input) {
  const parts = input.split(" ");
  const produktId = parts[1];
  const resultBox = document.getElementById("resultBox");

  if (!produktId) {
    resultBox.innerText = "❌ Bitte gib eine Produkt-ID an. Beispiel: getSimilarCheaperProduct B00123";
    return;
  }

  fetch("/getSimilarCheaperProduct?id=" + encodeURIComponent(produktId))
    .then((res) => res.json())
    .then((data) => {
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else if (data.length === 0) {
        resultBox.innerHTML = `<p>Keine günstigeren ähnlichen Produkte für ${produktId} gefunden.</p>`;
      } else {
        resultBox.innerHTML = `
          <table border="1" cellpadding="5" cellspacing="0">
            <thead>
              <tr>
                <th>Produkt-ID</th>
                <th>Titel</th>
                <th>Typ</th>
                <th>Billigster Preis</th>
              </tr>
            </thead>
            <tbody>
              ${data
                .map(
                  (p) => `
                    <tr>
                      <td>${p.produktId}</td>
                      <td>${p.titel}</td>
                      <td>${p.typ}</td>
                      <td>${parseFloat(p.billigsterPreis).toLocaleString("de-DE", {
                        style: "currency",
                        currency: "EUR",
                      })}</td>
                    </tr>`
                )
                .join("")}
            </tbody>
          </table>`;
      }
    })
    .catch((err) => {
      resultBox.innerText = "❌ Fehler beim Abrufen der Daten: " + err.message;
    });
}

function executeGetRezensionen(input) {
  const parts = input.split(" ");
  const produktId = parts.length > 1 ? parts[1] : "";
  const resultBox = document.getElementById("resultBox");

  if (!produktId) {
    resultBox.innerText = "❌ Bitte gib eine Produkt-ID an. Beispiel: getRezensionen B00123";
    return;
  }

  fetch("/getRezensionen?produktId=" + encodeURIComponent(produktId))
    .then((res) => res.json())
    .then((data) => {
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else if (data.length === 0) {
        resultBox.innerHTML = `<p>Keine Rezensionen für ${produktId} vorhanden.</p>`;
      } else {
        resultBox.innerHTML = `
          <table border="1" cellpadding="5" cellspacing="0">
            <thead>
              <tr>
                <th>Datum</th>
                <th>Punkte</th>
                <th>Username</th>
                <th>Zusammenfassung</th>
                <th>Text</th>
              </tr>
            </thead>
            <tbody>
              ${data
                .map(
                  (r) => `
                    <tr>
                      <td>${r.datum ?? "-"}</td>
                      <td>${r.punkte ?? "-"}</td>
                      <td>${r.username ?? "-"}</td>
                      <td>${r.zusammenfassung ?? "-"}</td>
                      <td>${r.text ?? "-"}</td>
                    </tr>`
                )
                .join("")}
            </tbody>
          </table>`;
      }
    })
    .catch((err) => {
      resultBox.innerText = "❌ Fehler beim Abrufen der Rezensionen: " + err.message;
    });
}

function executeAddNewReview(input) {
  // Beispiel: addNewReview B00123 5 max "top" "super text"
  const match = input.match(/^addNewReview\s+(\S+)\s+(\d+)\s+(\S+)\s+"([^"]+)"\s+"([^"]+)"$/);

  const resultBox = document.getElementById("resultBox");

  if (!match) {
    resultBox.innerText = "❌ Ungültiger Befehl. Beispiel:\naddNewReview B00123 5 max \"super\" \"text\"";
    return;
  }

  const [, produktId, punkte, username, zusammenfassung, text] = match;

  const payload = {
    produktId,
    username,
    punkte,
    zusammenfassung,
    text
  };

  fetch("/addNewReview", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  })
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        resultBox.innerText = "❌ Fehler: " + data.error;
      } else {
        resultBox.innerText = "✅ " + data.message;
      }
    })
    .catch(err => {
      resultBox.innerText = "❌ Netzwerkfehler: " + err.message;
    });
}

document.addEventListener("DOMContentLoaded", () => {
  updateDbStatus();
  
  const resultBox = document.getElementById("resultBox");
  resultBox.addEventListener("click", function (event) {
    if (event.target.classList.contains("caret")) {
      const nested = event.target.parentElement.querySelector(".nested");
      if (nested) nested.classList.toggle("active");
      event.target.classList.toggle("caret-down");
    }
  });
});