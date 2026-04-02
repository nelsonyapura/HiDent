const SCHEMAS = {
        FDI: {
            adult: {
                upper: [18,17,16,15,14,13,12,11, 21,22,23,24,25,26,27,28],
                lower: [48,47,46,45,44,43,42,41, 31,32,33,34,35,36,37,38]
            },
            child: {
                upper: [55,54,53,52,51, 61,62,63,64,65],
                lower: [85,84,83,82,81, 71,72,73,74,75]
            },
            mixed: {
                upper: [18,17,16,55,54,53,52,51, 61,62,63,64,65,26,27,28],
                lower: [48,47,46,85,84,83,82,81, 71,72,73,74,75,36,37,38]
            }
        },
        ADA: {
            adult: {
                upper: [1,2,3,4,5,6,7,8, 9,10,11,12,13,14,15,16],
                lower: [32,31,30,29,28,27,26,25, 24,23,22,21,20,19,18,17]
            },
            child: {
                upper: ['A','B','C','D','E', 'F','G','H','I','J'],
                lower: ['T','S','R','Q','P', 'O','N','M','L','K']
            },
            mixed: {
                upper: [1,2,3,'A','B','C','D','E', 'F','G','H','I','J',14,15,16],
                lower: [32,31,30,'T','S','R','Q','P', 'O','N','M','L','K',19,18,17]
            }
        }
    };

    const TOOTH_NAMES = {
        18:'3er molar',17:'2do molar',16:'1er molar',15:'2do premolar',14:'1er premolar',
        13:'Canino',12:'Inc. lateral',11:'Inc. central',
        21:'Inc. central',22:'Inc. lateral',23:'Canino',
        24:'1er premolar',25:'2do premolar',26:'1er molar',27:'2do molar',28:'3er molar',
        48:'3er molar',47:'2do molar',46:'1er molar',45:'2do premolar',44:'1er premolar',
        43:'Canino',42:'Inc. lateral',41:'Inc. central',
        31:'Inc. central',32:'Inc. lateral',33:'Canino',
        34:'1er premolar',35:'2do premolar',36:'1er molar',37:'2do molar',38:'3er molar',
        55:'2do molar d.',54:'1er molar d.',53:'Canino d.',52:'Inc. lat. d.',51:'Inc. cen. d.',
        61:'Inc. cen. d.',62:'Inc. lat. d.',63:'Canino d.',64:'1er molar d.',65:'2do molar d.',
        85:'2do molar d.',84:'1er molar d.',83:'Canino d.',82:'Inc. lat. d.',81:'Inc. cen. d.',
        71:'Inc. cen. d.',72:'Inc. lat. d.',73:'Canino d.',74:'1er molar d.',75:'2do molar d.'
    };

    const TOOTH_TYPE = {};
    [18,17,16,28,27,26,48,47,46,38,37,36,55,54,65,64,85,84,75,74].forEach(c => TOOTH_TYPE[c] = 'molar');
    [15,14,25,24,45,44,35,34].forEach(c => TOOTH_TYPE[c] = 'premolar');
    [13,23,43,33,53,63,83,73].forEach(c => TOOTH_TYPE[c] = 'canine');
    [12,22,42,32,52,62,82,72].forEach(c => TOOTH_TYPE[c] = 'lateral');
    [11,21,41,31,51,61,81,71].forEach(c => TOOTH_TYPE[c] = 'central');

    const CONDITION_LABELS = {
        CARIES:'Caries',
        CORONA_DEFINITIVA:'Corona Definitiva', CORONA_TEMPORAL:'Corona Temporal',
        OBTURACION_RESINA_BUENA:'Obt. Resina (buena)', OBTURACION_RESINA_MALA:'Obt. Resina (mala)',
        OBTURACION_AMALGAMA_BUENA:'Obt. Amalgama (buena)', OBTURACION_AMALGAMA_MALA:'Obt. Amalgama (mala)',
        RESTAURACION_TEMPORAL:'Restauración Temporal',
        CORONA:'Corona', CORONA_MAL_ESTADO:'Corona Mal Estado', ENDODONCIA:'Endodoncia',
        TRATAMIENTO_CONDUCTOS:'Trat. Conductos (TC)', PULPECTOMIA:'Pulpectomía (PC)', PULPOTOMIA:'Pulpotomía (PP)',
        IMPLANTE:'Implante', AUSENTE:'Ausente', EXTRACCION:'Extracción',
        FRACTURA:'Fractura',
        DESGASTE:'Desgaste Oclusal/Incisal', DISCROMIA:'Diente Discrómico',
        ECTOPICO:'Diente Ectópico', CLAVIJA:'Diente en Clavija',
        EXTRUIDO:'Diente Extruido', INTRUIDO:'Diente Intruido',
        IMPACTACION:'Impactación', SEMI_IMPACTACION:'Semi-impactación',
        MACRODONCIA:'Macrodoncia', MICRODONCIA:'Microdoncia',
        MOVILIDAD_1:'Movilidad Grado 1', MOVILIDAD_2:'Movilidad Grado 2', MOVILIDAD_3:'Movilidad Grado 3',
        REMANENTE_RADICULAR:'Remanente Radicular',
        GIROVERSION:'Giroversión', MIGRACION:'Migración',
        SUPERNUMERARIO:'Supernumerario',
        ORTODONCIA_FIJO:'Ap. Ortodóntico Fijo', ORTODONCIA_REMOVIBLE:'Ap. Ortodóntico Removible',
        DIASTEMA:'Diastema',
        EDENTULO_TOTAL:'Edéntulo Total',
        PROTESIS_REMOVIBLE:'Prótesis Removible', PROTESIS_TOTAL:'Prótesis Total',
        GEMINACION_FUSION:'Geminación/Fusión', TRANSPOSICION:'Transposición',
        OTRO:'Otro'
    };

    const CONDITION_COLORS = {
        CARIES:'#EF4444',
        CORONA_DEFINITIVA:'#2563EB', CORONA_TEMPORAL:'#EF4444',
        OBTURACION_RESINA_BUENA:'#16A34A', OBTURACION_RESINA_MALA:'#78350F',
        OBTURACION_AMALGAMA_BUENA:'#7C3AED', OBTURACION_AMALGAMA_MALA:'#F97316',
        RESTAURACION_TEMPORAL:'#EF4444',
        CORONA:'#EAB308', CORONA_MAL_ESTADO:'#DC2626', ENDODONCIA:'#EC4899',
        TRATAMIENTO_CONDUCTOS:'#2563EB', PULPECTOMIA:'#2563EB', PULPOTOMIA:'#2563EB',
        IMPLANTE:'#2563EB', AUSENTE:'#94A3B8', EXTRACCION:'#1F2937',
        FRACTURA:'#EF4444',
        DESGASTE:'#2563EB', DISCROMIA:'#2563EB',
        ECTOPICO:'#2563EB', CLAVIJA:'#2563EB',
        EXTRUIDO:'#2563EB', INTRUIDO:'#2563EB',
        IMPACTACION:'#2563EB', SEMI_IMPACTACION:'#2563EB',
        MACRODONCIA:'#2563EB', MICRODONCIA:'#2563EB',
        MOVILIDAD_1:'#2563EB', MOVILIDAD_2:'#2563EB', MOVILIDAD_3:'#2563EB',
        REMANENTE_RADICULAR:'#EF4444',
        GIROVERSION:'#2563EB', MIGRACION:'#2563EB',
        SUPERNUMERARIO:'#2563EB',
        ORTODONCIA_FIJO:'#2563EB', ORTODONCIA_REMOVIBLE:'#2563EB',
        DIASTEMA:'#2563EB',
        EDENTULO_TOTAL:'#2563EB',
        PROTESIS_REMOVIBLE:'#2563EB', PROTESIS_TOTAL:'#2563EB',
        GEMINACION_FUSION:'#2563EB', TRANSPOSICION:'#2563EB',
        OTRO:'#A855F7'
    };

    const LABEL_CONDITIONS = {
        DESGASTE:'DES', DISCROMIA:'DIS', ECTOPICO:'E',
        IMPACTACION:'I', SEMI_IMPACTACION:'SI',
        MACRODONCIA:'MAC', MICRODONCIA:'MIC',
        MOVILIDAD_1:'M1', MOVILIDAD_2:'M2', MOVILIDAD_3:'M3',
        IMPLANTE:'IMP', REMANENTE_RADICULAR:'RR',
        TRATAMIENTO_CONDUCTOS:'TC', PULPECTOMIA:'PC', PULPOTOMIA:'PP'
    };

    const RANGE_CONDITIONS = [
        'ORTODONCIA_FIJO','ORTODONCIA_REMOVIBLE','DIASTEMA',
        'EDENTULO_TOTAL','PROTESIS_REMOVIBLE','PROTESIS_TOTAL',
        'GEMINACION_FUSION','SUPERNUMERARIO','TRANSPOSICION','MIGRACION'
    ];

    const NO_SURFACE_CONDITIONS = [
        'CORONA_DEFINITIVA','CORONA_TEMPORAL','CORONA_MAL_ESTADO','AUSENTE','EXTRACCION',
        'DESGASTE','DISCROMIA','ECTOPICO','CLAVIJA','EXTRUIDO','INTRUIDO',
        'IMPACTACION','SEMI_IMPACTACION','MACRODONCIA','MICRODONCIA',
        'MOVILIDAD_1','MOVILIDAD_2','MOVILIDAD_3','REMANENTE_RADICULAR',
        'IMPLANTE','GIROVERSION',
        'TRATAMIENTO_CONDUCTOS','PULPECTOMIA','PULPOTOMIA',
        ...RANGE_CONDITIONS
    ];

    let currentNomenclature = 'FDI';
    let currentDentition    = 'adult';
    let selectedTooth       = null;
    let toothData           = {};
    let pendingChanges      = [];
    let currentMode         = 'normal';
    let rangeStart          = null;
    let rangeEnd            = null;

    function setMode(mode) {
        currentMode = mode; rangeStart = null; rangeEnd = null; selectedTooth = null;
        document.querySelectorAll('.mode-btn').forEach(b => b.classList.remove('active'));
        var btnMap = {normal:'modeNormal',corona:'modeCorona',fijo:'modeFijo',removible:'modeRemovible',rango:'modeRango'};
        var el = document.getElementById(btnMap[mode]);
        if (el) el.classList.add('active');
        document.querySelectorAll('.sub-panel').forEach(p => p.classList.remove('active'));
        var panelMap = {normal:'panelNormal',corona:'panelCorona',fijo:'panelFijo',removible:'panelRemovible',rango:'panelRango'};
        var panel = document.getElementById(panelMap[mode]);
        if (panel) panel.classList.add('active');
        if (mode === 'corona') {
            document.getElementById('coronaToothDisplay').textContent = '— Haz clic en un diente —';
            document.getElementById('coronaToothDisplay').style.color = '#94A3B8';
            document.getElementById('btnApplyCorona').disabled = true;
        }
        if (mode === 'fijo') {
            resetRangeDisplay('fijo');
        }
        if (mode === 'removible') {
            resetRangeDisplay('rem');
        }
        if (mode === 'rango') {
            resetRangeDisplay('rango');
        }
        if (mode === 'normal') {
            document.getElementById('noSelectionMsg').style.display = '';
            document.getElementById('toothEditPanel').style.display = 'none';
        }
        document.querySelectorAll('.tooth-cell').forEach(c => {
            c.classList.remove('selected','range-start','range-end');
        });
    }

    function resetRangeDisplay(prefix) {
        var s = document.getElementById(prefix + 'StartDisplay');
        var e = document.getElementById(prefix + 'EndDisplay');
        if (s) { s.textContent = '—'; s.style.color = '#94A3B8'; }
        if (e) { e.textContent = '—'; e.style.color = '#94A3B8'; }
        var btn = document.getElementById('btnApply' + prefix.charAt(0).toUpperCase() + prefix.slice(1));
        if (btn) btn.disabled = true;
    }

    function createToothSVG(code, conditions, arch, previewSurfaces, previewColor) {
        const ns = 'http:
        const type = TOOTH_TYPE[code] || 'premolar';
        const CW = 46, CH = 46, W = 54;
        const rootCfg = {
            molar:{roots:3,height:42,crownW:48}, premolar:{roots:2,height:36,crownW:42},
            canine:{roots:1,height:40,crownW:38}, lateral:{roots:1,height:34,crownW:36},
            central:{roots:1,height:30,crownW:40}
        };
        const cfg = rootCfg[type];
        const rH = cfg.height, cW = cfg.crownW;
        const totalH = CH + rH + 4;
        const cxAdj = (W - cW) / 2;

        const surfaceConditions = (conditions||[]).filter(c =>
            !RANGE_CONDITIONS.includes(c.condition) &&
            !NO_SURFACE_CONDITIONS.includes(c.condition));

        const sf = (surface) => {
            if (previewSurfaces && previewColor && previewSurfaces.includes(surface)) return previewColor;
            for (const c of surfaceConditions) {
                if ((c.surfaces||[]).includes(surface)) return CONDITION_COLORS[c.condition]||'#E83E8C';
            }

            const tempRest = (conditions||[]).find(c => c.condition === 'RESTAURACION_TEMPORAL');
            if (tempRest && (tempRest.surfaces||[]).includes(surface)) return 'none';
            return '#fff';
        };

        const ssf = (surface) => {
            const tempRest = (conditions||[]).find(c => c.condition === 'RESTAURACION_TEMPORAL');
            if (tempRest && (tempRest.surfaces||[]).includes(surface)) return '#EF4444';
            return '#9CA3AF';
        };
        const sswf = (surface) => {
            const tempRest = (conditions||[]).find(c => c.condition === 'RESTAURACION_TEMPORAL');
            if (tempRest && (tempRest.surfaces||[]).includes(surface)) return '2';
            return '1';
        };

        const svg = document.createElementNS(ns, 'svg');
        svg.setAttribute('class','tooth-svg');
        svg.setAttribute('viewBox','0 0 '+W+' '+totalH);
        svg.setAttribute('width',W);
        svg.setAttribute('height',totalH);

        const stroke = '#9CA3AF', sw = '1';
        const rootY = arch==='upper' ? 0 : CH+2;
        const crownY = arch==='upper' ? rH+2 : 0;

        const drawRoot = (cx2, rw, extraH) => {
            const p = document.createElementNS(ns,'path');
            if (arch==='upper') {
                p.setAttribute('d','M'+(cx2-rw/2)+','+(rootY+rH)+' L'+cx2+','+(rootY+2-(extraH||0))+' L'+(cx2+rw/2)+','+(rootY+rH)+' Z');
            } else {
                p.setAttribute('d','M'+(cx2-rw/2)+','+rootY+' L'+cx2+','+(rootY+rH-2+(extraH||0))+' L'+(cx2+rw/2)+','+rootY+' Z');
            }
            p.setAttribute('fill','#fff'); p.setAttribute('stroke',stroke); p.setAttribute('stroke-width',sw);
            return p;
        };
        if (cfg.roots===1) { svg.appendChild(drawRoot(W/2, cW*0.3, 0)); }
        else if (cfg.roots===2) { [W/2-cW*0.24, W/2+cW*0.24].forEach(cx2 => svg.appendChild(drawRoot(cx2, cW*0.2, 0))); }
        else { [W/2-cW*0.28, W/2, W/2+cW*0.28].forEach((cx2,i) => svg.appendChild(drawRoot(cx2, cW*0.17, i===1?-4:0))); }

        const hasPulpar = (conditions||[]).find(c => ['TRATAMIENTO_CONDUCTOS','PULPECTOMIA','PULPOTOMIA','ENDODONCIA'].includes(c.condition));
        if (hasPulpar) {
            const line = document.createElementNS(ns,'line');
            line.setAttribute('x1',W/2); line.setAttribute('x2',W/2);
            if (arch==='upper') { line.setAttribute('y1',rootY+4); line.setAttribute('y2',rootY+rH-2); }
            else { line.setAttribute('y1',rootY+2); line.setAttribute('y2',rootY+rH-4); }
            line.setAttribute('stroke','#2563EB'); line.setAttribute('stroke-width','2.5');
            line.setAttribute('stroke-linecap','round');
            svg.appendChild(line);
        }

        const hasRR = (conditions||[]).find(c => c.condition==='REMANENTE_RADICULAR');
        if (hasRR) {
            const txt = document.createElementNS(ns,'text');
            txt.setAttribute('x',W/2); txt.setAttribute('text-anchor','middle');
            txt.setAttribute('y', arch==='upper' ? rootY+rH/2+4 : rootY+rH/2+4);
            txt.setAttribute('fill','#EF4444'); txt.setAttribute('font-size','11');
            txt.setAttribute('font-weight','800'); txt.setAttribute('font-family','Outfit,sans-serif');
            txt.textContent = 'RR';
            svg.appendChild(txt);
        }

        const x1=cxAdj, y1=crownY, x2=cxAdj+cW, y2=crownY+CH;
        const inset=cW*0.25;
        const ix1=x1+inset, iy1=y1+inset, ix2=x2-inset, iy2=y2-inset;
        const vS='V', lpS=arch==='upper'?'P':'L';

        const mkPoly = (pts, surface) => {
            const p = document.createElementNS(ns,'polygon');
            p.setAttribute('points',pts); p.setAttribute('fill',sf(surface));
            p.setAttribute('stroke',ssf(surface)); p.setAttribute('stroke-width',sswf(surface));
            return p;
        };
        svg.appendChild(mkPoly(x1+','+y1+' '+x2+','+y1+' '+ix2+','+iy1+' '+ix1+','+iy1, arch==='upper'?vS:lpS));
        svg.appendChild(mkPoly(x1+','+y2+' '+ix1+','+iy2+' '+ix2+','+iy2+' '+x2+','+y2, arch==='upper'?lpS:vS));
        svg.appendChild(mkPoly(x1+','+y1+' '+ix1+','+iy1+' '+ix1+','+iy2+' '+x1+','+y2, 'M'));
        svg.appendChild(mkPoly(x2+','+y1+' '+x2+','+y2+' '+ix2+','+iy2+' '+ix2+','+iy1, 'D'));

        const center = document.createElementNS(ns,'rect');
        center.setAttribute('x',ix1); center.setAttribute('y',iy1);
        center.setAttribute('width',ix2-ix1); center.setAttribute('height',iy2-iy1);
        center.setAttribute('fill',sf('O')); center.setAttribute('stroke',ssf('O'));
        center.setAttribute('stroke-width',sswf('O'));
        svg.appendChild(center);

        const outline = document.createElementNS(ns,'rect');
        outline.setAttribute('x',x1); outline.setAttribute('y',y1);
        outline.setAttribute('width',cW); outline.setAttribute('height',CH);
        outline.setAttribute('fill','none'); outline.setAttribute('stroke',stroke);
        outline.setAttribute('stroke-width','1.2');
        svg.appendChild(outline);

        if ((conditions||[]).find(c => c.condition==='CORONA_DEFINITIVA')) {
            const el = document.createElementNS(ns,'ellipse');
            el.setAttribute('cx',(x1+x2)/2); el.setAttribute('cy',(y1+y2)/2);
            el.setAttribute('rx',cW/2+3); el.setAttribute('ry',CH/2+3);
            el.setAttribute('fill','none'); el.setAttribute('stroke','#2563EB');
            el.setAttribute('stroke-width','2');
            svg.appendChild(el);
        }

        if ((conditions||[]).find(c => c.condition==='CORONA_TEMPORAL')) {
            const el = document.createElementNS(ns,'ellipse');
            el.setAttribute('cx',(x1+x2)/2); el.setAttribute('cy',(y1+y2)/2);
            el.setAttribute('rx',cW/2+3); el.setAttribute('ry',CH/2+3);
            el.setAttribute('fill','none'); el.setAttribute('stroke','#EF4444');
            el.setAttribute('stroke-width','2');
            svg.appendChild(el);
        }

        if ((conditions||[]).find(c => c.condition==='CORONA_MAL_ESTADO')) {
            const el = document.createElementNS(ns,'ellipse');
            el.setAttribute('cx',(x1+x2)/2); el.setAttribute('cy',(y1+y2)/2);
            el.setAttribute('rx',cW/2+3); el.setAttribute('ry',CH/2+3);
            el.setAttribute('fill','none'); el.setAttribute('stroke','#DC2626');
            el.setAttribute('stroke-width','2.5');
            svg.appendChild(el);
            const cx = (x1+x2)/2, cy = (y1+y2)/2, r = cW/2-2;
            const x1l = document.createElementNS(ns,'line');
            x1l.setAttribute('x1',cx-r); x1l.setAttribute('y1',cy-r);
            x1l.setAttribute('x2',cx+r); x1l.setAttribute('y2',cy+r);
            x1l.setAttribute('stroke','#DC2626'); x1l.setAttribute('stroke-width','1.8');
            x1l.setAttribute('stroke-linecap','round');
            svg.appendChild(x1l);
            const x2l = document.createElementNS(ns,'line');
            x2l.setAttribute('x1',cx+r); x2l.setAttribute('y1',cy-r);
            x2l.setAttribute('x2',cx-r); x2l.setAttribute('y2',cy+r);
            x2l.setAttribute('stroke','#DC2626'); x2l.setAttribute('stroke-width','1.8');
            x2l.setAttribute('stroke-linecap','round');
            svg.appendChild(x2l);
        }

        if ((conditions||[]).find(c => c.condition==='CLAVIJA')) {
            const numY = arch==='upper' ? crownY-6 : crownY+CH+14;
            const tri = document.createElementNS(ns,'polygon');
            tri.setAttribute('points',
                (W/2)+','+(numY-10)+' '+(W/2-10)+','+(numY+4)+' '+(W/2+10)+','+(numY+4));
            tri.setAttribute('fill','none'); tri.setAttribute('stroke','#2563EB');
            tri.setAttribute('stroke-width','1.8');
            svg.appendChild(tri);
        }

        if ((conditions||[]).find(c => c.condition==='EXTRUIDO')) {
            const arrowY = arch==='upper' ? crownY+CH+6 : crownY-6;
            const dir = arch==='upper' ? -1 : 1;
            const line = document.createElementNS(ns,'line');
            line.setAttribute('x1',W/2); line.setAttribute('y1',arrowY);
            line.setAttribute('x2',W/2); line.setAttribute('y2',arrowY+dir*12);
            line.setAttribute('stroke','#2563EB'); line.setAttribute('stroke-width','2');
            svg.appendChild(line);
            const head = document.createElementNS(ns,'polygon');
            const tipY = arrowY+dir*12;
            head.setAttribute('points',
                W/2+','+tipY+' '+(W/2-4)+','+(tipY-dir*5)+' '+(W/2+4)+','+(tipY-dir*5));
            head.setAttribute('fill','#2563EB');
            svg.appendChild(head);
        }

        if ((conditions||[]).find(c => c.condition==='INTRUIDO')) {
            const arrowY = arch==='upper' ? crownY-2 : crownY+CH+2;
            const dir = arch==='upper' ? -1 : 1;
            const line = document.createElementNS(ns,'line');
            line.setAttribute('x1',W/2); line.setAttribute('y1',arrowY);
            line.setAttribute('x2',W/2); line.setAttribute('y2',arrowY+dir*12);
            line.setAttribute('stroke','#2563EB'); line.setAttribute('stroke-width','2');
            svg.appendChild(line);
            const head = document.createElementNS(ns,'polygon');
            const tipY = arrowY+dir*12;
            head.setAttribute('points',
                W/2+','+tipY+' '+(W/2-4)+','+(tipY-dir*5)+' '+(W/2+4)+','+(tipY-dir*5));
            head.setAttribute('fill','#2563EB');
            svg.appendChild(head);
        }

        if ((conditions||[]).find(c => c.condition==='GIROVERSION')) {
            const gy = arch==='upper' ? y2+4 : y1-4;
            const path = document.createElementNS(ns,'path');
            path.setAttribute('d','M'+(W/2-10)+','+gy+' Q'+W/2+','+(gy+(arch==='upper'?8:-8))+' '+(W/2+10)+','+gy);
            path.setAttribute('fill','none'); path.setAttribute('stroke','#2563EB');
            path.setAttribute('stroke-width','1.8'); path.setAttribute('marker-end','url(#arrowBlue)');
            svg.appendChild(path);

            if (!svg.querySelector('#arrowBlue')) {
                const defs = document.createElementNS(ns,'defs');
                const marker = document.createElementNS(ns,'marker');
                marker.setAttribute('id','arrowBlue'); marker.setAttribute('viewBox','0 0 10 10');
                marker.setAttribute('refX','8'); marker.setAttribute('refY','5');
                marker.setAttribute('markerWidth','6'); marker.setAttribute('markerHeight','6');
                marker.setAttribute('orient','auto-start-reverse');
                const mp = document.createElementNS(ns,'path');
                mp.setAttribute('d','M 0 0 L 10 5 L 0 10 z'); mp.setAttribute('fill','#2563EB');
                marker.appendChild(mp); defs.appendChild(marker); svg.insertBefore(defs, svg.firstChild);
            }
        }

        if ((conditions||[]).find(c => c.condition==='FRACTURA')) {
            const fl = document.createElementNS(ns,'line');
            fl.setAttribute('x1',x1+2); fl.setAttribute('y1',y1+2);
            fl.setAttribute('x2',x2-2); fl.setAttribute('y2',y2-2);
            fl.setAttribute('stroke','#EF4444'); fl.setAttribute('stroke-width','2.5');
            fl.setAttribute('stroke-linecap','round');
            svg.appendChild(fl);
        }

        return svg;
    }

    function renderRow(containerId, codes, arch) {
        const container = document.getElementById(containerId);
        container.innerHTML = '';
        const midlineAt = codes.length / 2;

        codes.forEach((code, i) => {
            if (i === midlineAt) {
                const ml = document.createElement('div');
                ml.className = 'midline'; ml.style.height = '100px';
                container.appendChild(ml);
            }
            const conditions = toothData[String(code)] || [];
            const normalConds = conditions.filter(c => !RANGE_CONDITIONS.includes(c.condition)
                && c.condition!=='CORONA_DEFINITIVA' && c.condition!=='CORONA_TEMPORAL');
            const firstCond = normalConds.length ? normalConds[0].condition : '';

            const labels = [];
            conditions.forEach(c => {
                if (LABEL_CONDITIONS[c.condition]) labels.push({text:LABEL_CONDITIONS[c.condition], color:CONDITION_COLORS[c.condition]||'#2563EB'});
                if (c.condition==='CORONA_DEFINITIVA') labels.push({text:c.note||'CD', color:'#2563EB'});
                if (c.condition==='CORONA_TEMPORAL') labels.push({text:'CT', color:'#EF4444'});
            });

            const cell = document.createElement('div');
            cell.className = 'tooth-cell' +
                (firstCond ? ' has-condition cond-'+firstCond : '') +
                (selectedTooth && selectedTooth.code===String(code) ? ' selected' : '');
            if (labels.length > 0 && !firstCond) cell.classList.add('has-condition');
            cell.dataset.code = String(code);
            cell.dataset.arch = arch;
            cell.addEventListener('click', () => onToothClick(String(code), arch));

            const numDiv = document.createElement('div');
            numDiv.className = 'tooth-number';
            numDiv.textContent = code;

            const dot = document.createElement('div');
            dot.className = 'tooth-dot';
            const dotColor = CONDITION_COLORS[firstCond] || (labels.length>0 ? labels[0].color : 'transparent');
            dot.style.background = dotColor;

            const svg = createToothSVG(code, conditions, arch);

            if (arch === 'upper') {
                labels.forEach(lb => {
                    const d = document.createElement('div');
                    d.className = 'tooth-label-box';
                    d.style.color = lb.color; d.style.borderColor = lb.color;
                    d.style.background = lb.color==='#EF4444' ? '#FEF2F2' : '#EFF6FF';
                    d.textContent = lb.text;
                    cell.appendChild(d);
                });
                cell.appendChild(numDiv); cell.appendChild(svg);
            } else {
                cell.appendChild(svg); cell.appendChild(numDiv);
                labels.forEach(lb => {
                    const d = document.createElement('div');
                    d.className = 'tooth-label-box';
                    d.style.color = lb.color; d.style.borderColor = lb.color;
                    d.style.background = lb.color==='#EF4444' ? '#FEF2F2' : '#EFF6FF';
                    d.textContent = lb.text;
                    cell.appendChild(d);
                });
            }
            cell.appendChild(dot);

            if (firstCond==='AUSENTE') {
                const m = document.createElement('div');
                m.className = 'absent-mark'; m.textContent = ''; cell.appendChild(m);
            }
            if (firstCond==='EXTRACCION') {
                const l = document.createElement('div');
                l.className = 'extract-line'; cell.appendChild(l);
            }
            container.appendChild(cell);
        });
    }

    function renderArches() {
        const schema = SCHEMAS[currentNomenclature][currentDentition];
        renderRow('upperArch', schema.upper, 'upper');
        renderRow('lowerArch', schema.lower, 'lower');
        setTimeout(() => {
            renderRangeOverlays('upperArch', schema.upper, 'upper');
            renderRangeOverlays('lowerArch', schema.lower, 'lower');
        }, 50);
        updateTreatmentPlan();
    }

    function renderRangeOverlays(containerId, codes, arch) {
        const container = document.getElementById(containerId);
        container.querySelectorAll('.arch-overlay').forEach(o => o.remove());

        const rangeItems = [];
        codes.forEach(code => {
            (toothData[String(code)]||[]).forEach(c => {
                if (RANGE_CONDITIONS.includes(c.condition) && c.toothCodeEnd) {
                    rangeItems.push({startCode:String(code), endCode:c.toothCodeEnd,
                        condition:c.condition, color:c.color||CONDITION_COLORS[c.condition]||'#2563EB'});
                }
            });
        });
        if (rangeItems.length===0) return;

        const containerRect = container.getBoundingClientRect();
        const ns = 'http:
        const svg = document.createElementNS(ns,'svg');
        svg.setAttribute('class','arch-overlay');
        svg.style.height = containerRect.height+'px';

        rangeItems.forEach(item => {
            const sCell = container.querySelector('.tooth-cell[data-code="'+item.startCode+'"]');
            const eCell = container.querySelector('.tooth-cell[data-code="'+item.endCode+'"]');
            if (!sCell || !eCell) return;
            const sr = sCell.getBoundingClientRect();
            const er = eCell.getBoundingClientRect();
            const sCx = sr.left+sr.width/2-containerRect.left;
            const eCx = er.left+er.width/2-containerRect.left;
            const minX = Math.min(sCx,eCx), maxX = Math.max(sCx,eCx);
            const color = item.color;

            if (item.condition==='ORTODONCIA_FIJO') {
                drawFijoOverlay(svg, ns, sCx, eCx, arch, containerRect.height, color);
            } else if (item.condition==='ORTODONCIA_REMOVIBLE') {
                drawZigzagOverlay(svg, ns, minX, maxX, arch, containerRect.height, color);
            } else if (item.condition==='DIASTEMA') {
                drawDiastemaOverlay(svg, ns, sCx, eCx, arch, containerRect.height, color);
            } else if (item.condition==='EDENTULO_TOTAL') {
                drawLineOverlay(svg, ns, minX, maxX, arch, containerRect.height, color, 'crown', 1);
            } else if (item.condition==='PROTESIS_REMOVIBLE') {
                drawLineOverlay(svg, ns, minX, maxX, arch, containerRect.height, color, 'apex', 2);
            } else if (item.condition==='PROTESIS_TOTAL') {
                drawLineOverlay(svg, ns, minX, maxX, arch, containerRect.height, color, 'crown', 2);
            } else if (item.condition==='GEMINACION_FUSION') {
                drawGeminacionOverlay(svg, ns, sCx, eCx, arch, containerRect.height, color);
            } else if (item.condition==='SUPERNUMERARIO') {
                drawSupernumerarioOverlay(svg, ns, sCx, eCx, arch, containerRect.height, color);
            } else if (item.condition==='TRANSPOSICION') {
                drawTransposicionOverlay(svg, ns, sCx, eCx, arch, containerRect.height, color);
            } else if (item.condition==='MIGRACION') {
                drawMigracionOverlay(svg, ns, sCx, eCx, arch, containerRect.height, color);
            }
        });
        container.appendChild(svg);
    }

    function drawFijoOverlay(svg, ns, sCx, eCx, arch, h, color) {
        const y = arch==='upper' ? 4 : h-18;
        const sq = 14;
        [sCx,eCx].forEach(cx => {
            var r = document.createElementNS(ns,'rect');
            r.setAttribute('x',cx-sq/2); r.setAttribute('y',y); r.setAttribute('width',sq); r.setAttribute('height',sq);
            r.setAttribute('fill','none'); r.setAttribute('stroke',color); r.setAttribute('stroke-width','1.8'); r.setAttribute('rx','1');
            svg.appendChild(r);
            var h1 = document.createElementNS(ns,'line');
            h1.setAttribute('x1',cx-3); h1.setAttribute('y1',y+sq/2); h1.setAttribute('x2',cx+3); h1.setAttribute('y2',y+sq/2);
            h1.setAttribute('stroke',color); h1.setAttribute('stroke-width','1.5'); svg.appendChild(h1);
            var v1 = document.createElementNS(ns,'line');
            v1.setAttribute('x1',cx); v1.setAttribute('y1',y+sq/2-3); v1.setAttribute('x2',cx); v1.setAttribute('y2',y+sq/2+3);
            v1.setAttribute('stroke',color); v1.setAttribute('stroke-width','1.5'); svg.appendChild(v1);
        });
        var line = document.createElementNS(ns,'line');
        line.setAttribute('x1',Math.min(sCx,eCx)+sq/2); line.setAttribute('y1',y+sq/2);
        line.setAttribute('x2',Math.max(sCx,eCx)-sq/2); line.setAttribute('y2',y+sq/2);
        line.setAttribute('stroke',color); line.setAttribute('stroke-width','1.8'); svg.appendChild(line);
    }

    function drawZigzagOverlay(svg, ns, minX, maxX, arch, h, color) {
        const y = arch==='upper' ? 8 : h-12;
        const segCount = Math.max(Math.round((maxX-minX)/12),4);
        const segW = (maxX-minX)/segCount;
        let d = 'M'+minX+','+y;
        for (let k=0; k<segCount; k++) {
            d += ' L'+(minX+segW*(k+0.5))+','+(k%2===0?y-7:y+7);
        }
        d += ' L'+maxX+','+y;
        var p = document.createElementNS(ns,'path');
        p.setAttribute('d',d); p.setAttribute('fill','none'); p.setAttribute('stroke',color);
        p.setAttribute('stroke-width','2'); p.setAttribute('stroke-linecap','round'); svg.appendChild(p);
    }

    function drawDiastemaOverlay(svg, ns, sCx, eCx, arch, h, color) {
        const midX = (sCx+eCx)/2;
        const midY = h/2;
        const p = document.createElementNS(ns,'path');
        p.setAttribute('d','M'+(midX-6)+','+(midY-14)+' Q'+(midX-10)+','+midY+' '+(midX-6)+','+(midY+14)+
            ' M'+(midX+6)+','+(midY-14)+' Q'+(midX+10)+','+midY+' '+(midX+6)+','+(midY+14));
        p.setAttribute('fill','none'); p.setAttribute('stroke',color); p.setAttribute('stroke-width','2');
        svg.appendChild(p);
    }

    function drawLineOverlay(svg, ns, minX, maxX, arch, h, color, position, lineCount) {
        const baseY = position==='crown'
            ? (arch==='upper' ? h*0.55 : h*0.45)
            : (arch==='upper' ? 8 : h-8);
        for (let i=0; i<lineCount; i++) {
            var line = document.createElementNS(ns,'line');
            line.setAttribute('x1',minX-4); line.setAttribute('y1',baseY+i*5);
            line.setAttribute('x2',maxX+4); line.setAttribute('y2',baseY+i*5);
            line.setAttribute('stroke',color); line.setAttribute('stroke-width','2');
            line.setAttribute('stroke-linecap','round'); svg.appendChild(line);
        }
    }

    function drawGeminacionOverlay(svg, ns, sCx, eCx, arch, h, color) {
        const midY = arch==='upper' ? 2 : h-2;
        const r = 14;
        [sCx,eCx].forEach(cx => {
            var c = document.createElementNS(ns,'circle');
            c.setAttribute('cx',cx); c.setAttribute('cy',midY); c.setAttribute('r',r);
            c.setAttribute('fill','none'); c.setAttribute('stroke',color); c.setAttribute('stroke-width','1.8');
            svg.appendChild(c);
        });
    }

    function drawSupernumerarioOverlay(svg, ns, sCx, eCx, arch, h, color) {
        const midX = (sCx+eCx)/2;
        const midY = arch==='upper' ? 8 : h-8;
        var c = document.createElementNS(ns,'circle');
        c.setAttribute('cx',midX); c.setAttribute('cy',midY); c.setAttribute('r',10);
        c.setAttribute('fill','none'); c.setAttribute('stroke',color); c.setAttribute('stroke-width','1.8');
        svg.appendChild(c);
        var t = document.createElementNS(ns,'text');
        t.setAttribute('x',midX); t.setAttribute('y',midY+4); t.setAttribute('text-anchor','middle');
        t.setAttribute('fill',color); t.setAttribute('font-size','11'); t.setAttribute('font-weight','800');
        t.textContent = 'S'; svg.appendChild(t);
    }

    function drawTransposicionOverlay(svg, ns, sCx, eCx, arch, h, color) {
        const y = arch==='upper' ? 0 : h;
        const p1 = document.createElementNS(ns,'path');
        p1.setAttribute('d','M'+sCx+','+y+' Q'+((sCx+eCx)/2)+','+(y+(arch==='upper'?14:-14))+' '+eCx+','+y);
        p1.setAttribute('fill','none'); p1.setAttribute('stroke',color); p1.setAttribute('stroke-width','1.8');
        svg.appendChild(p1);
        const p2 = document.createElementNS(ns,'path');
        p2.setAttribute('d','M'+eCx+','+y+' Q'+((sCx+eCx)/2)+','+(y+(arch==='upper'?-10:10))+' '+sCx+','+y);
        p2.setAttribute('fill','none'); p2.setAttribute('stroke',color); p2.setAttribute('stroke-width','1.8');
        svg.appendChild(p2);
    }

    function drawMigracionOverlay(svg, ns, sCx, eCx, arch, h, color) {
        const y = h/2;
        var line = document.createElementNS(ns,'line');
        line.setAttribute('x1',sCx); line.setAttribute('y1',y);
        line.setAttribute('x2',eCx); line.setAttribute('y2',y);
        line.setAttribute('stroke',color); line.setAttribute('stroke-width','2'); svg.appendChild(line);

        const dir = eCx > sCx ? 1 : -1;
        var head = document.createElementNS(ns,'polygon');
        head.setAttribute('points', eCx+','+y+' '+(eCx-dir*6)+','+(y-4)+' '+(eCx-dir*6)+','+(y+4));
        head.setAttribute('fill',color); svg.appendChild(head);
    }

    function onToothClick(code, arch) {
        if (currentMode==='normal') selectTooth(code, arch);
        else if (currentMode==='corona') selectToothForCorona(code);
        else if (currentMode==='fijo') selectToothForRange(code, 'fijo');
        else if (currentMode==='removible') selectToothForRange(code, 'rem');
        else if (currentMode==='rango') selectToothForRange(code, 'rango');
    }

    function selectToothForCorona(code) {
        document.querySelectorAll('.tooth-cell').forEach(c => c.classList.remove('selected'));
        var cell = document.querySelector('.tooth-cell[data-code="'+code+'"]');
        if (cell) cell.classList.add('selected');
        document.getElementById('coronaToothDisplay').textContent = code+' — '+(TOOTH_NAMES[code]||'');
        document.getElementById('coronaToothDisplay').style.color = '#1E293B';
        document.getElementById('btnApplyCorona').disabled = false;
        selectedTooth = {code};
    }

    function selectToothForRange(code, prefix) {
        if (!rangeStart) {
            rangeStart = code; rangeEnd = null;
            document.querySelectorAll('.tooth-cell').forEach(c => c.classList.remove('range-start','range-end'));
            var cell = document.querySelector('.tooth-cell[data-code="'+code+'"]');
            if (cell) cell.classList.add('range-start');
            var s = document.getElementById(prefix+'StartDisplay');
            if (s) { s.textContent = code; s.style.color = '#1E293B'; }
            var e = document.getElementById(prefix+'EndDisplay');
            if (e) { e.textContent = '— clic 2do diente —'; e.style.color = '#94A3B8'; }
        } else {
            rangeEnd = code;
            var cell = document.querySelector('.tooth-cell[data-code="'+code+'"]');
            if (cell) cell.classList.add('range-end');
            var e = document.getElementById(prefix+'EndDisplay');
            if (e) { e.textContent = code; e.style.color = '#1E293B'; }
            var btn = document.getElementById('btnApply'+prefix.charAt(0).toUpperCase()+prefix.slice(1));
            if (btn) btn.disabled = false;
        }
    }

    function applyCorona() {
        if (!selectedTooth) return;
        var code = selectedTooth.code;
        var crownType = document.getElementById('coronaTypeSelect').value;
        var condType = document.getElementById('coronaCondType') ? document.getElementById('coronaCondType').value : 'CORONA_DEFINITIVA';
        if (!toothData[code]) toothData[code] = [];
        toothData[code] = toothData[code].filter(c => c.condition!=='CORONA_DEFINITIVA' && c.condition!=='CORONA_TEMPORAL' && c.condition!=='CORONA_MAL_ESTADO');
        toothData[code].push({condition:condType, surfaces:[], note:condType==='CORONA_DEFINITIVA'?crownType:'CT', createdAt:new Date().toISOString()});
        pendingChanges.push({action:'save',toothCode:code,nomenclature:currentNomenclature,condition:condType,surfaces:'[]',note:condType==='CORONA_DEFINITIVA'?crownType:'CT'});
        renderArches();
        showToast((condType==='CORONA_DEFINITIVA'?'Corona '+crownType:'Corona Temporal')+' → diente '+code,'success');
        document.getElementById('coronaToothDisplay').textContent = '— Haz clic en un diente —';
        document.getElementById('coronaToothDisplay').style.color = '#94A3B8';
        document.getElementById('btnApplyCorona').disabled = true;
        selectedTooth = null;
        document.querySelectorAll('.tooth-cell').forEach(c => c.classList.remove('selected'));
    }

    function applyFijo() { applyRangeCondition('ORTODONCIA_FIJO', 'fijo'); }
    function applyRemovible() { applyRangeCondition('ORTODONCIA_REMOVIBLE', 'rem'); }

    function applyRangoGeneral() {
        if (!rangeStart || !rangeEnd) return;
        var condition = document.getElementById('rangoConditionSelect').value;
        if (!condition) { showToast('Selecciona un tipo de condición','error'); return; }
        var estado = document.getElementById('rangoEstadoSelect').value;
        var color = estado==='malo' ? '#EF4444' : '#2563EB';
        if (!toothData[rangeStart]) toothData[rangeStart] = [];
        toothData[rangeStart].push({
            condition:condition, surfaces:[], note:CONDITION_LABELS[condition]+' → '+rangeEnd,
            toothCodeEnd:rangeEnd, color:color, createdAt:new Date().toISOString()
        });
        pendingChanges.push({action:'save',toothCode:rangeStart,toothCodeEnd:rangeEnd,
            nomenclature:currentNomenclature,condition:condition,surfaces:'[]',color:color,
            note:'estado:'+estado});
        renderArches();
        showToast(CONDITION_LABELS[condition]+': '+rangeStart+' → '+rangeEnd,'success');
        rangeStart=null; rangeEnd=null;
        document.querySelectorAll('.tooth-cell').forEach(c => c.classList.remove('range-start','range-end'));
        resetRangeDisplay('rango');
    }

    function applyRangeCondition(condition, prefix) {
        if (!rangeStart || !rangeEnd) return;
        var estadoEl = document.getElementById(prefix+'EstadoSelect');
        var estado = estadoEl ? estadoEl.value : 'bueno';
        var color = estado==='malo' ? '#EF4444' : '#2563EB';
        if (!toothData[rangeStart]) toothData[rangeStart] = [];
        toothData[rangeStart].push({
            condition:condition, surfaces:[], note:CONDITION_LABELS[condition]+' → '+rangeEnd,
            toothCodeEnd:rangeEnd, color:color, createdAt:new Date().toISOString()
        });
        pendingChanges.push({action:'save',toothCode:rangeStart,toothCodeEnd:rangeEnd,
            nomenclature:currentNomenclature,condition:condition,surfaces:'[]',color:color,
            note:'estado:'+estado});
        renderArches();
        showToast(CONDITION_LABELS[condition]+': '+rangeStart+' → '+rangeEnd,'success');
        rangeStart=null; rangeEnd=null;
        document.querySelectorAll('.tooth-cell').forEach(c => c.classList.remove('range-start','range-end'));
        resetRangeDisplay(prefix);
    }

    function selectTooth(code, arch) {
        selectedTooth = {code, arch};
        document.querySelectorAll('.tooth-cell').forEach(c => c.classList.remove('selected'));
        const cell = document.querySelector('.tooth-cell[data-code="'+code+'"]');
        if (cell) cell.classList.add('selected');
        document.getElementById('noSelectionMsg').style.display = 'none';
        document.getElementById('toothEditPanel').style.display = '';
        document.getElementById('toothEditNum').textContent = code;
        document.getElementById('toothEditName').textContent = TOOTH_NAMES[code]||('Diente '+code);
        document.getElementById('toothEditArch').textContent = arch==='upper'?'Arcada superior':'Arcada inferior';
        document.getElementById('conditionSelect').value = '';
        document.getElementById('toothNote').value = '';
        document.querySelectorAll('.surface-btn').forEach(b => b.classList.remove('active'));
        document.getElementById('surfacesGroup').style.display = '';
        renderToothConditionsList(code);
    }

    function renderToothConditionsList(code) {
        const container = document.getElementById('currentConditionsList');
        if (!container) return;
        const conditions = toothData[code]||[];
        if (conditions.length===0) { container.innerHTML = '<p class="text-muted small mb-0">Sin condiciones registradas.</p>'; return; }
        container.innerHTML = conditions.map(c => {
            const dot = '<span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:'+(CONDITION_COLORS[c.condition]||c.color||'#999')+';margin-right:5px;flex-shrink:0;"></span>';
            const label = CONDITION_LABELS[c.condition]||c.condition;
            const extra = c.toothCodeEnd ? ' → '+c.toothCodeEnd : '';
            const noteText = c.note ? ' · '+c.note : '';
            return '<div class="d-flex align-items-center gap-1 p-1 rounded mb-1" style="background:#F8FAFC;font-size:11px;">'+
                '<div class="d-flex align-items-center flex-fill gap-1" style="min-width:0;">'+dot+
                '<span class="fw-semibold">'+label+extra+'</span>'+
                '<span class="text-muted ms-1">'+noteText+'</span></div>'+
                '<button class="btn btn-outline-danger btn-sm py-0 px-1" style="font-size:10px;line-height:1.4;" onclick="removeCondition(\''+code+'\',\''+c.condition+'\')">'+
                '<i class="bi bi-x"></i></button></div>';
        }).join('');
    }

    function removeCondition(code, condition) {
        if (!confirm('¿Eliminar '+(CONDITION_LABELS[condition]||condition)+' del diente '+code+'?')) return;
        if (toothData[code]) {
            toothData[code] = toothData[code].filter(c => c.condition!==condition);
            if (toothData[code].length===0) delete toothData[code];
        }
        pendingChanges.push({action:'delete',toothCode:code,nomenclature:currentNomenclature,condition});
        renderArches();
        if (selectedTooth && selectedTooth.code===code) renderToothConditionsList(code);
    }

    function applyToothChanges() {
        if (!selectedTooth) return;
        const code = selectedTooth.code;
        const condition = document.getElementById('conditionSelect').value;
        if (!condition) { showToast('Selecciona una condición','error'); return; }
        const surfaces = NO_SURFACE_CONDITIONS.includes(condition) ? [] :
            [...document.querySelectorAll('.surface-btn.active')].map(b => b.dataset.surface);
        const note = document.getElementById('toothNote').value;
        if (!toothData[code]) toothData[code] = [];
        const idx = toothData[code].findIndex(c => c.condition===condition);
        const entry = {condition, surfaces, note, createdAt:new Date().toISOString()};
        if (idx>=0) toothData[code][idx] = entry; else toothData[code].push(entry);
        pendingChanges.push({action:'save',toothCode:code,nomenclature:currentNomenclature,
            condition,surfaces:JSON.stringify(surfaces),note});
        renderArches();
        document.getElementById('conditionSelect').value = '';
        document.getElementById('toothNote').value = '';
        document.querySelectorAll('.surface-btn').forEach(b => b.classList.remove('active'));
        document.getElementById('surfacesGroup').style.display = '';
        renderToothConditionsList(code);
    }

    function clearCurrentTooth() {
        if (!selectedTooth) return;
        if (!confirm('¿Eliminar TODAS las condiciones del diente '+selectedTooth.code+'?')) return;
        delete toothData[selectedTooth.code];
        pendingChanges.push({action:'deleteAll',toothCode:selectedTooth.code,nomenclature:currentNomenclature});
        document.getElementById('conditionSelect').value = '';
        document.getElementById('toothNote').value = '';
        document.querySelectorAll('.surface-btn').forEach(b => b.classList.remove('active'));
        renderArches();
        renderToothConditionsList(selectedTooth.code);
    }

    document.getElementById('conditionSelect').addEventListener('change', function() {
        var cond = this.value;
        var group = document.getElementById('surfacesGroup');
        if (group) group.style.display = NO_SURFACE_CONDITIONS.includes(cond) ? 'none' : '';
        previewSelectedSurfaces();
    });

    async function saveAllPending() {
        if (pendingChanges.length===0) { showToast('Sin cambios pendientes','info'); return; }
        let saved = 0;
        for (const op of pendingChanges) {
            try {
                if (op.action==='deleteAll') {
                    await fetch('/api/odontogram/'+PATIENT_ID+'/tooth/'+op.toothCode+'?nomenclature='+op.nomenclature,{method:'DELETE'});
                } else if (op.action==='delete') {
                    await fetch('/api/odontogram/'+PATIENT_ID+'/tooth/'+op.toothCode+'/condition/'+op.condition+'?nomenclature='+op.nomenclature,{method:'DELETE'});
                } else {
                    var body = {toothCode:op.toothCode,nomenclature:op.nomenclature,condition:op.condition,surfaces:op.surfaces,note:op.note};
                    if (op.toothCodeEnd) body.toothCodeEnd = op.toothCodeEnd;
                    if (op.color) body.color = op.color;
                    await fetch('/api/odontogram/'+PATIENT_ID+'/tooth',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
                }
                saved++;
            } catch(e) { console.error(e); }
        }
        pendingChanges = [];
        showToast('Guardado ('+saved+' operación'+(saved!==1?'es':'')+')', 'success');
    }

    async function loadOdontogram() {
        try {
            const res = await fetch('/api/odontogram/'+PATIENT_ID+'?nomenclature='+currentNomenclature);
            if (!res.ok) { if (res.status===401) { window.location.href='/auth/login'; return; } throw new Error('HTTP '+res.status); }
            const ct = res.headers.get('content-type')||'';
            if (!ct.includes('application/json')) throw new Error('No JSON');
            const data = await res.json();
            toothData = {};
            data.forEach(t => {
                let surfaces = []; try { surfaces = JSON.parse(t.surfaces||'[]'); } catch(e) {}
                if (!toothData[t.toothCode]) toothData[t.toothCode] = [];
                toothData[t.toothCode].push({
                    condition:t.condition||'', surfaces, note:t.note||'',
                    color:t.color||null, toothCodeEnd:t.toothCodeEnd||null, createdAt:t.createdAt||null
                });
            });
        } catch(e) { console.error('Error cargando:',e); }
        renderArches();
    }

    function updateTreatmentPlan() {
        const tbody = document.getElementById('treatmentBody');
        tbody.querySelectorAll('tr.treatment-row').forEach(r => r.remove());
        const rows = [];
        Object.entries(toothData).forEach(([code, conditions]) => {
            (conditions||[]).forEach(c => {
                if (c.condition) {
                    var extra = c.toothCodeEnd ? ' → '+c.toothCodeEnd : '';
                    rows.push({code,...c,displayLabel:(CONDITION_LABELS[c.condition]||c.condition)+extra});
                }
            });
        });
        document.getElementById('treatmentCount').textContent = rows.length;
        document.getElementById('treatmentEmpty').style.display = rows.length===0 ? '' : 'none';
        rows.forEach(({code,condition,surfaces,note,createdAt,displayLabel,color}) => {
            const tr = document.createElement('tr');
            tr.className = 'treatment-row'; tr.style.cursor = 'pointer';
            const dotColor = color||CONDITION_COLORS[condition]||'#999';
            const fecha = createdAt ? new Date(createdAt).toLocaleDateString('es-PE',{day:'2-digit',month:'2-digit',year:'numeric'}) : '—';
            const notaText = note ? '<span class="note-preview" onclick="event.stopPropagation();showNoteModal(\''+code+'\',\''+(displayLabel).replace(/'/g,"\\'")+'\',this)" data-note="'+note.replace(/"/g,'&quot;').replace(/\n/g,'&#10;')+'"><i class="bi bi-chat-left-text me-1" style="font-size:10px;"></i>'+(note.length>20?note.substring(0,20)+'…':note)+'</span>' : '—';
            tr.innerHTML = '<td><strong>'+code+'</strong></td><td><span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:'+dotColor+';margin-right:4px;"></span>'+displayLabel+'</td><td>'+((surfaces||[]).join(', ')||'—')+'</td><td>'+notaText+'</td><td style="font-size:11px;color:#64748B;">'+fecha+'</td><td><button class="btn btn-outline-danger btn-sm py-0 px-1" style="font-size:10px;" onclick="removeCondition(\''+code+'\',\''+condition+'\')"><i class="bi bi-x"></i></button></td>';
            tr.addEventListener('click',e => { if (e.target.closest('button')) return; const cell = document.querySelector('.tooth-cell[data-code="'+code+'"]'); if (cell) { setMode('normal'); selectTooth(code,cell.dataset.arch); }});
            tbody.appendChild(tr);
        });
    }

    function showToast(msg, type) {
        const bg = type==='success'?'#10B981':type==='info'?'#3B82F6':'#EF4444';
        const t = document.createElement('div');
        t.style.cssText = 'position:fixed;bottom:24px;right:24px;background:'+bg+';color:#fff;padding:10px 18px;border-radius:10px;font-size:13px;font-weight:600;box-shadow:0 4px 12px rgba(0,0,0,.15);z-index:9999;opacity:0;transition:opacity .3s;';
        t.textContent = msg; document.body.appendChild(t);
        requestAnimationFrame(() => { t.style.opacity = 1; });
        setTimeout(() => { t.style.opacity = 0; setTimeout(() => t.remove(), 300); }, 2800);
    }

    function toggleLegend() { const p = document.getElementById('legendPanel'); p.style.display = p.style.display==='none'?'block':'none'; }

    function showNoteModal(toothCode, conditionLabel, el) {
        const note = el.getAttribute('data-note').replace(/&#10;/g,'\n');
        document.getElementById('noteModalToothCode').textContent = toothCode;
        document.getElementById('noteModalCondition').textContent = conditionLabel;
        document.getElementById('noteModalContent').textContent = note;
        new bootstrap.Modal(document.getElementById('noteModal')).show();
    }

    document.querySelectorAll('.surface-btn').forEach(btn => btn.addEventListener('click', () => { btn.classList.toggle('active'); previewSelectedSurfaces(); }));

    function previewSelectedSurfaces() {
        if (!selectedTooth || currentMode!=='normal') return;
        const code = selectedTooth.code, arch = selectedTooth.arch;
        const condition = document.getElementById('conditionSelect').value;
        const surfaces = [...document.querySelectorAll('.surface-btn.active')].map(b => b.dataset.surface);
        const color = condition ? (CONDITION_COLORS[condition]||'#E83E8C') : '#F9A8D4';
        const conditions = toothData[String(code)]||[];
        const cell = document.querySelector('.tooth-cell[data-code="'+code+'"]');
        if (!cell) return;
        const oldSvg = cell.querySelector('.tooth-svg');
        if (!oldSvg) return;
        const newSvg = createToothSVG(code, conditions, arch, surfaces.length>0?surfaces:null, surfaces.length>0?color:null);
        oldSvg.replaceWith(newSvg);
    }

    document.getElementById('nomenclature').addEventListener('change', e => {
        currentNomenclature = e.target.value; toothData = {}; pendingChanges = [];
        selectedTooth = null; rangeStart = null; rangeEnd = null;
        setMode('normal'); loadOdontogram();
    });
    document.getElementById('dentitionType').addEventListener('change', e => { currentDentition = e.target.value; renderArches(); });

    loadOdontogram();
