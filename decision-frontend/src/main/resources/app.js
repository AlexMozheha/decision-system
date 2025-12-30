// --- INITIAL DATA ---
let factors = [
    { name: 'Price', unitOfMeasure: 'USD', type: 'OBJECTIVE', factorWeight: 0.5, isGrowing: false },
    { name: 'Quality', unitOfMeasure: 'points', type: 'OBJECTIVE', factorWeight: 0.3, isGrowing: true },
    { name: 'DeliverySpeed', unitOfMeasure: 'days', type: 'OBJECTIVE', factorWeight: 0.2, isGrowing: false }
];

let alternatives = [
    { name: 'Supplier A', description: 'Reliable but more expensive', riskCoefficient: 0.2, values: {} },
    { name: 'Supplier B', description: 'Cheaper, medium quality', riskCoefficient: 0.15, values: {} },
    { name: 'Supplier C', description: 'Fast delivery, mid price', riskCoefficient: 0.25, values: {} }
];

// Заповнення початковими значеннями (як у Вашому прикладі)
alternatives[0].values = { 'Price': { rawValue: 1200, score: null }, 'Quality': { rawValue: 8, score: null }, 'DeliverySpeed': { rawValue: 5, score: null } };
alternatives[1].values = { 'Price': { rawValue: 900, score: null }, 'Quality': { rawValue: 6, score: null }, 'DeliverySpeed': { rawValue: 7, score: null } };
alternatives[2].values = { 'Price': { rawValue: 1000, score: null }, 'Quality': { rawValue: 7, score: null }, 'DeliverySpeed': { rawValue: 3, score: null } };


// --- UTILITIES ---

// Функція для оновлення полів вводу залежно від типу фактора
function updateFactorInputState(factorIndex) {
    const factorType = document.getElementById(`factor-type-${factorIndex}`).value;
    const isObjective = factorType === 'OBJECTIVE';

    // Проходимо по всіх альтернативах, щоб оновити поля вводу
    alternatives.forEach((alt, altIndex) => {
        const rawInput = document.getElementById(`alt-${altIndex}-factor-${factorIndex}-raw`);
        const scoreInput = document.getElementById(`alt-${altIndex}-factor-${factorIndex}-score`);

        if (rawInput && scoreInput) {
            // OBJECTIVE: вводимо rawValue, блокуємо score
            rawInput.disabled = !isObjective;
            rawInput.style.backgroundColor = isObjective ? '#f0f8ff' : '#eee';
            
            scoreInput.disabled = isObjective;
            scoreInput.style.backgroundColor = isObjective ? '#eee' : '#fffacd';
            
            // Очищення блокованого поля
            if (isObjective) { scoreInput.value = ''; } else { rawInput.value = ''; }
        }
    });
}


// --- DYNAMIC TABLE GENERATION ---

function renderTable() {
    const factorRow = document.getElementById('factorRow');
    const factorDetailRow = document.getElementById('factorDetailRow');
    const valueHeaderRow = document.getElementById('valueHeaderRow');
    const alternativesBody = document.getElementById('alternativesBody');
    
    // Очищення попередніх даних
    factorRow.innerHTML = '<th rowspan="3">Альтернатива / Параметр</th>';
    factorDetailRow.innerHTML = '';
    valueHeaderRow.innerHTML = '';
    alternativesBody.innerHTML = '';

    // 1. Рядок заголовків ФАКТОРІВ
    factors.forEach((f, fIndex) => {
        // Заголовок з назвою
        factorRow.innerHTML += `<th colspan="2" class="factor-header">
            <input type="text" id="factor-name-${fIndex}" value="${f.name}" placeholder="Назва Фактора" onchange="factors[${fIndex}].name = this.value">
        </th>`;
        
        // Рядок деталей фактора (Вага, Тип, Growing)
        factorDetailRow.innerHTML += `
            <td colspan="2" class="factor-header">
                <div class="factor-detail-item">
                Вага: 
                <input type="number" id="factor-weight-${fIndex}" value="${f.factorWeight}" step="0.01" min="0" max="1" onchange="factors[${fIndex}].factorWeight = parseFloat(this.value)"> <br>
                </div>
                <div class="factor-detail-item">
                Тип:
                <select id="factor-type-${fIndex}" onchange="factors[${fIndex}].type = this.value; updateFactorInputState(${fIndex})">
                    <option value="OBJECTIVE" ${f.type === 'OBJECTIVE' ? 'selected' : ''}>OBJECTIVE</option>
                    <option value="SUBJECTIVE" ${f.type === 'SUBJECTIVE' ? 'selected' : ''}>SUBJECTIVE</option>
                </select><br>
                </div>
                <div class="factor-detail-item">
                Зростає? 
                <select onchange="factors[${fIndex}].isGrowing = (this.value === 'true')">
                    <option value="true" ${f.isGrowing ? 'selected' : ''}>Так (більше краще)</option>
                    <option value="false" ${!f.isGrowing ? 'selected' : ''}>Ні (менше краще)</option>
                </select>
                </div>
                <button onclick="removeFactor(${fIndex})">X</button>
            </td>
        `;
        valueHeaderRow.innerHTML += '<th>Raw Value</th><th>Score</th>';
    });


    // 2. Рядки АЛЬТЕРНАТИВ
    alternatives.forEach((alt, altIndex) => {
        let row = `<tr id="alternative-${altIndex}" class="alternative-row">
            <td>
                Назва: <input type="text" value="${alt.name}" onchange="alternatives[${altIndex}].name = this.value"><br>
                Ризик: <input type="number" value="${alt.riskCoefficient}" step="0.01" min="0" max="1" onchange="alternatives[${altIndex}].riskCoefficient = parseFloat(this.value)">
                <button onclick="removeAlternative(${altIndex})">X</button>
            </td>`;

        // Клітинки для оцінок
        factors.forEach((f, fIndex) => {
            const factorName = f.name;
            const evalData = alt.values[factorName] || { rawValue: '', score: '' };

            // Перевірка типу фактора для блокування полів
            const isObjective = f.type === 'OBJECTIVE';
            const rawDisabled = !isObjective;
            const scoreDisabled = isObjective;
            
            const rawValue = isObjective ? evalData.rawValue : '';
            const scoreValue = !isObjective ? evalData.score : '';

            row += `
                <td><input type="number" 
                           id="alt-${altIndex}-factor-${fIndex}-raw"
                           class="raw-input"
                           value="${rawValue}" 
                           ${rawDisabled ? 'disabled' : ''}
                           style="background-color: ${rawDisabled ? '#eee' : '#f0f8ff'};"
                           onchange="updateAlternativeValue(${altIndex}, '${factorName}', 'rawValue', parseFloat(this.value))"></td>
                <td><input type="number" 
                           id="alt-${altIndex}-factor-${fIndex}-score"
                           class="score-input"
                           value="${scoreValue}" 
                           ${scoreDisabled ? 'disabled' : ''}
                           style="background-color: ${scoreDisabled ? '#eee' : '#fffacd'};"
                           onchange="updateAlternativeValue(${altIndex}, '${factorName}', 'score', parseFloat(this.value))"></td>
            `;
        });
        
        row += '</tr>';
        alternativesBody.innerHTML += row;
    });

    // Обов'язкове оновлення стану після рендерингу
    factors.forEach((_, fIndex) => updateFactorInputState(fIndex));
}


function addFactor() {
    factors.push({ name: `New Factor ${factors.length + 1}`, unitOfMeasure: 'N/A', type: 'OBJECTIVE', factorWeight: 0.1, isGrowing: true });
    // Ініціалізуємо нові значення для нових факторів в існуючих альтернативах
    alternatives.forEach(alt => {
        alt.values[`New Factor ${factors.length}`] = { rawValue: 0, score: null };
    });
    renderTable();
}

function removeFactor(index) {
    const factorName = factors[index].name;
    factors.splice(index, 1);
    // Видалення значень цього фактора з усіх альтернатив
    alternatives.forEach(alt => {
        delete alt.values[factorName];
    });
    renderTable();
}

function addAlternative() {
    let newValues = {};
    factors.forEach(f => {
        newValues[f.name] = { rawValue: null, score: null };
    });

    alternatives.push({ 
        name: `New Alternative ${alternatives.length + 1}`, 
        description: 'New Description', 
        riskCoefficient: 0.0, 
        values: newValues 
    });
    renderTable();
}

function removeAlternative(index) {
    alternatives.splice(index, 1);
    renderTable();
}

function updateAlternativeValue(altIndex, factorName, type, value) {
    if (!alternatives[altIndex].values[factorName]) {
        alternatives[altIndex].values[factorName] = { rawValue: null, score: null };
    }
    alternatives[altIndex].values[factorName][type] = value;
}


// --- REQUEST LOGIC ---

function buildRequestPayload() {
    const factorParams = factors.map(f => ({
        name: f.name,
        unitOfMeasure: f.unitOfMeasure,
        type: f.type,
        description: f.description || '',
        factorWeight: f.factorWeight,
        isGrowing: f.isGrowing
    }));

    const altPayload = alternatives.map(alt => {
        const values = factors.map(f => {
            const value = alt.values[f.name] || { rawValue: null, score: null };
            
            // Logic to handle null/undefined based on factor type
            const rawValue = f.type === 'OBJECTIVE' ? value.rawValue : null;
            const score = f.type === 'SUBJECTIVE' ? value.score : null;

            return {
                factorName: f.name,
                rawValue: rawValue,
                score: score
            };
        });

        return {
            name: alt.name,
            description: alt.description,
            riskCoefficient: alt.riskCoefficient,
            values: values
        };
    });

    return {
        decisionName: document.getElementById('decisionName').value,
        method: document.getElementById('method').value,
        maxScore: parseFloat(document.getElementById('maxScore').value),
        factorParams: factorParams,
        alternatives: altPayload
    };
}

function displayDecisionResults(data) {
    const resultsDisplay = document.getElementById('resultsDisplay');

    if (!data || !data.alternatives) {
        resultsDisplay.innerHTML = '<p style="color: red;">Помилка: Некоректний формат відповіді.</p>';
        return;
    }

    let html = `
        <p><strong>Назва рішення:</strong> ${data.decisionName}</p>
        <p><strong>Обчислено:</strong> ${new Date(data.calculatedAt).toLocaleString()}</p>
        <h4>Рейтинг Альтернатив:</h4>
        <table class="results-table">
            <thead>
                <tr>
                    <th>Назва Альтернативи</th>
                    <th>Зважений Бал (Weighted Score)</th>
                    <th>Бал з урахуванням Ризику (Risk Adjusted Score)</th>
                    <th>Рівень Ризику</th>
                    <th>Рекомендація</th>
                </tr>
            </thead>
            <tbody>
    `;

    data.alternatives.forEach(alt => {
        const rowClass = alt.isRecommended ? 'style="background-color: #d4edda;"' : '';
        const recommendationText = alt.isRecommended ? 'Так' : 'Ні';

        html += `
            <tr ${rowClass}>
                <td style="text-align: left;">${alt.name}</td>
                <td>${alt.weightedScore.toFixed(4)}</td>
                <td>${alt.riskAdjustedScore.toFixed(4)}</td>
                <td>${alt.riskLevel}</td>
                <td><strong>${recommendationText}</strong></td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    resultsDisplay.innerHTML = html;
}


async function sendDecisionRequest() {
    const payload = buildRequestPayload();
    const resultsDisplay = document.getElementById('resultsDisplay');
    resultsDisplay.textContent = 'Обчислення...';

    const ENDPOINT = 'http://localhost:8081/api/decisions/make-decision'; 

    
    console.log("Sending Payload:", JSON.stringify(payload, null, 2));

    try {
        const response = await fetch(ENDPOINT, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();
        
        if (!response.ok) {
            resultsDisplay.textContent = `Помилка ${response.status}: ${JSON.stringify(data, null, 2)}`;
        } else {
            displayDecisionResults(data);
        }
        
    } catch (error) {
        resultsDisplay.textContent = `Помилка мережі або обробки: ${error.message}`;
        console.error('Fetch error:', error);
    }
}

// Початкове завантаження таблиці
window.onload = renderTable;