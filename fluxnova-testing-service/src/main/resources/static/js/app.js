document.addEventListener('DOMContentLoaded', () => {
    // Elements
    const processSelect = document.getElementById('process-select');
    const instanceCount = document.getElementById('instance-count');
    const processVariables = document.getElementById('process-variables');
    const loadTestForm = document.getElementById('load-test-form');
    const btnLaunch = document.getElementById('btn-launch');
    const btnRefreshStats = document.getElementById('btn-refresh-stats');
    
    // Stats Elements
    const statDefinitions = document.getElementById('stat-definitions');
    const statTestsRun = document.getElementById('stat-tests-run');
    const statSuccesses = document.getElementById('stat-successes');
    const statFailures = document.getElementById('stat-failures');
    
    // Monitor Elements
    const monitorEmptyState = document.getElementById('monitor-empty-state');
    const monitorActiveState = document.getElementById('monitor-active-state');
    const monitorKey = document.getElementById('monitor-key');
    const monitorRatio = document.getElementById('monitor-ratio');
    const monitorStatus = document.getElementById('monitor-status');
    const progressPercentage = document.getElementById('progress-percentage');
    const circle = document.querySelector('.progress-ring__circle');
    
    // History Elements
    const historyTbody = document.getElementById('history-tbody');
    
    // Config SVG progress ring
    const radius = circle.r.baseVal.value;
    const circumference = radius * 2 * Math.PI;
    circle.style.strokeDasharray = `${circumference} ${circumference}`;
    circle.style.strokeDashoffset = circumference;

    function setProgress(percent) {
        const offset = circumference - (percent / 100) * circumference;
        circle.style.strokeDashoffset = offset;
        progressPercentage.textContent = `${Math.round(percent)}%`;
    }

    // Load Data on Startup
    loadDefinitions();
    loadStats();
    loadHistory();

    // Event listeners
    btnRefreshStats.addEventListener('click', () => {
        loadStats();
        loadHistory();
    });

    loadTestForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const processKey = processSelect.value;
        const count = parseInt(instanceCount.value);
        let variables = {};
        
        try {
            if (processVariables.value.trim() !== "") {
                variables = JSON.parse(processVariables.value);
            }
        } catch (err) {
            alert('Invalid JSON variables format. Please check your syntax.');
            return;
        }

        // Show monitoring state
        monitorEmptyState.classList.add('hidden');
        monitorActiveState.classList.remove('hidden');
        monitorKey.textContent = processKey;
        monitorRatio.textContent = `0 / ${count}`;
        monitorStatus.textContent = 'RUNNING';
        monitorStatus.className = 'badge badge-active';
        setProgress(0);
        
        // Button state
        btnLaunch.disabled = true;
        btnLaunch.querySelector('.btn-text').textContent = 'Launching...';
        btnLaunch.querySelector('.loader').classList.remove('hidden');

        // Simple fake progress animation for UI smoothness while calling API
        let simulatedProgress = 0;
        const progressInterval = setInterval(() => {
            if (simulatedProgress < 85) {
                simulatedProgress += Math.random() * 8;
                setProgress(Math.min(simulatedProgress, 88));
            }
        }, 120);

        try {
            const response = await fetch('/api/test/run', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ processKey, count, variables })
            });

            clearInterval(progressInterval);
            
            if (response.ok) {
                const data = await response.json();
                setProgress(100);
                monitorRatio.textContent = `${data.successes} / ${data.totalRequested}`;
                
                if (data.failures > 0) {
                    monitorStatus.textContent = `COMPLETED WITH ERRORS`;
                    monitorStatus.className = 'badge badge-danger';
                } else {
                    monitorStatus.textContent = 'SUCCESSFUL';
                    monitorStatus.className = 'badge badge-success';
                }
                
                // Reload other sections
                loadStats();
                loadHistory();
            } else {
                throw new Error('Server returned an error');
            }
        } catch (error) {
            clearInterval(progressInterval);
            setProgress(0);
            monitorStatus.textContent = 'FAILED';
            monitorStatus.className = 'badge badge-danger';
            alert('Failed to execute load test: ' + error.message);
        } finally {
            btnLaunch.disabled = false;
            btnLaunch.querySelector('.btn-text').textContent = 'Run Load Test';
            btnLaunch.querySelector('.loader').classList.add('hidden');
        }
    });

    // Helper functions
    async function loadDefinitions() {
        try {
            const response = await fetch('/api/test/definitions');
            if (response.ok) {
                const data = await response.json();
                processSelect.innerHTML = '<option value="" disabled selected>Select a process...</option>';
                if (data.length === 0) {
                    processSelect.innerHTML = '<option value="" disabled>No deployed processes found</option>';
                    return;
                }
                data.forEach(def => {
                    const option = document.createElement('option');
                    option.value = def.key;
                    option.textContent = `${def.name || def.key} (v${def.version})`;
                    processSelect.appendChild(option);
                });
            }
        } catch (err) {
            console.error('Error loading process definitions', err);
            processSelect.innerHTML = '<option value="" disabled>Error loading definitions</option>';
        }
    }

    async function loadStats() {
        try {
            const response = await fetch('/api/test/stats');
            if (response.ok) {
                const data = await response.json();
                statDefinitions.textContent = data.totalProcessDefinitions;
                statTestsRun.textContent = data.totalTestsRun;
                statSuccesses.textContent = data.totalSuccesses;
                statFailures.textContent = data.totalFailures;
            }
        } catch (err) {
            console.error('Error loading stats', err);
        }
    }

    async function loadHistory() {
        try {
            const response = await fetch('/api/test/history');
            if (response.ok) {
                const data = await response.json();
                historyTbody.innerHTML = '';
                
                if (data.length === 0) {
                    historyTbody.innerHTML = '<tr><td colspan="7" class="text-center">No tests have been executed yet.</td></tr>';
                    return;
                }

                data.forEach(run => {
                    const tr = document.createElement('tr');
                    
                    // Format time
                    const runTime = new Date(run.startedAt).toLocaleString();
                    
                    tr.innerHTML = `
                        <td style="font-family: monospace; font-size: 13px;">${run.id.substring(0, 8)}...</td>
                        <strong><td>${run.processKey}</td></strong>
                        <td>${run.totalRequested}</td>
                        <td><span class="badge badge-success">${run.successes}</span></td>
                        <td><span class="badge ${run.failures > 0 ? 'badge-danger' : ''}">${run.failures}</span></td>
                        <td>${run.durationMs}ms</td>
                        <td style="color: var(--color-text-muted); font-size: 14px;">${runTime}</td>
                    `;
                    historyTbody.appendChild(tr);
                });
            }
        } catch (err) {
            console.error('Error loading history', err);
            historyTbody.innerHTML = '<tr><td colspan="7" class="text-center text-danger">Error loading history.</td></tr>';
        }
    }
});
