function calcAge() {
            var d = document.getElementById('birthDateInput');
            var ageInput = document.getElementById('ageInput');
            var chk = document.getElementById('isMinorCheck');
            var s   = document.getElementById('guardianSection');
            if (!d.value) { ageInput.value = ''; return; }
            var b = new Date(d.value), t = new Date();
            var age = t.getFullYear() - b.getFullYear();
            var m = t.getMonth() - b.getMonth();
            if (m < 0 || (m === 0 && t.getDate() < b.getDate())) age--;
            ageInput.value = age;
            if (age < 18) { chk.checked = true; s.classList.remove('d-none'); }
        }
        function toggleGuardian() {
            document.getElementById('guardianSection')
                .classList.toggle('d-none', !document.getElementById('isMinorCheck').checked);
        }
