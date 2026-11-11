// Controlled service responses verify the integration contract; these are test fixtures, not demo scores.
import {chromium} from '@playwright/test';
import assert from 'node:assert/strict';
import {packingTips} from '../src/packingTips.ts';
const browser=await chromium.launch({executablePath:'C:/Program Files/Google/Chrome/Application/chrome.exe',headless:true});
const page=await browser.newPage({viewport:{width:390,height:844}});
const b=name=>page.getByRole('button',{name,exact:true});
const nav=name=>page.getByRole('navigation').getByRole('button',{name,exact:true});
let mode='valid',requests=[];
await page.route('**/v1/packing-review',async route=>{
 const body=route.request().postDataJSON();requests.push(body);
 if(mode==='pending')return;
 if(mode==='connection')return route.abort('connectionfailed');
 if(mode==='quota')return route.fulfill({status:429,body:'quota'});
 if(mode==='invalid')return route.fulfill({json:{score:12,feedback:'Invalid score'}});
 if(mode==='unassessable')return route.fulfill({json:{score:null,feedback:'No se distingue el equipaje.'}});
 return route.fulfill({json:{score:8,feedback:'Respuesta de prueba: equipaje visible.'}});
});
try{
await page.goto('http://127.0.0.1:5174');await b('[ CREAR CUENTA ]').click();await page.getByLabel('USUARIO',{exact:true}).fill('reviewtest');await page.getByLabel('CONTRASEÑA',{exact:true}).fill('clave123');await page.getByLabel('CONFIRMAR CONTRASEÑA',{exact:true}).fill('clave123');await b('[ REGISTRAR ]').click();await b('CONFIRMAR MEDIDAS').click();await b('CONTINUAR AL CATÁLOGO').click();await nav('RESUMEN').click();
const images=await page.evaluate(()=>[ [100,200], [200,100] ].map(([w,h])=>{const c=document.createElement('canvas');c.width=w;c.height=h;return c.toDataURL('image/png').split(',')[1];}));
await b('AÑADIR FOTOS').click({noWaitAfter:true});await page.getByTestId('photo-picker').setInputFiles(images.map((s,i)=>({name:`photo-${i}.png`,mimeType:'image/png',buffer:Buffer.from(s,'base64')})));await page.locator('.review-photos img').nth(1).waitFor();
assert.equal(requests.length,0);await page.getByRole('button',{name:'Ver organización 2',exact:true}).click();assert.equal(requests.length,0);await page.getByText('Foto lista para evaluar. Pulsa EVALUAR FOTO CON IA para obtener tu puntuación.',{exact:true}).waitFor();await b('EVALUAR FOTO CON IA').click();await page.getByText('PUNTUACIÓN DE LA IA · 8/10',{exact:true}).waitFor();
assert.equal(requests.length,1);assert.deepEqual(Object.keys(requests[0]).sort(),['language','photoBase64','schemaVersion']);assert.equal(requests[0].language,'es');
const sentSize=await page.evaluate(async base64=>{const im=new Image();im.src='data:image/jpeg;base64,'+base64;await im.decode();return [im.naturalWidth,im.naturalHeight];},requests[0].photoBase64);assert.deepEqual(sentSize,[200,100]);
const advice=await page.locator('.general-tips li').allTextContents();assert.equal(advice.length,2);assert.notEqual(advice[0],advice[1]);const indices=advice.map(t=>packingTips.findIndex(pair=>pair[0]===t));assert.equal(indices.includes(-1),false);
await b('CERRAR').click();await page.getByRole('button',{name:'Ver organización 2',exact:true}).click();assert.deepEqual(await page.locator('.general-tips li').allTextContents(),advice);await b('CERRAR').click();
await nav('AJUSTES').click();await page.getByRole('button',{name:'IDIOMA ESPAÑOL ›'}).click();await b('[ CLOSE ]').click();await page.getByRole('button',{name:'View organization 2',exact:true}).click();assert.deepEqual(await page.locator('.general-tips li').allTextContents(),indices.map(i=>packingTips[i][1]));assert.equal(requests.length,1);await b('CLOSE').click();
await nav('SETTINGS').click();await page.getByRole('button',{name:'LANGUAGE ENGLISH ›'}).click();await b('[ CERRAR ]').click();await page.getByRole('button',{name:'Ver organización 1',exact:true}).click();assert.equal(await page.locator('.general-tips').count(),0);
mode='unassessable';await b('EVALUAR FOTO CON IA').click();await page.getByText('No se distingue el equipaje.',{exact:true}).waitFor();assert.equal(await page.getByText('PUNTUACIÓN DE LA IA · 8/10',{exact:true}).count(),0);assert.equal(await page.locator('.general-tips').count(),0);
mode='invalid';await b('EVALUAR FOTO CON IA').click();await page.getByText('La respuesta no es válida. Intenta evaluar de nuevo.',{exact:true}).waitFor();
mode='quota';await b('EVALUAR FOTO CON IA').click();await page.getByText('Cuota de evaluación agotada. Intenta más tarde.',{exact:true}).waitFor();
mode='connection';await b('EVALUAR FOTO CON IA').click();await page.getByText('No se pudo conectar con el servicio. La vista local sigue disponible.',{exact:true}).waitFor();
mode='pending';await b('EVALUAR FOTO CON IA').click();await page.getByText('La IA está evaluando la foto…',{exact:true}).waitFor();assert.equal(await b('EVALUAR FOTO CON IA').isDisabled(),true);await b('CANCELAR').click();await page.getByText('Evaluación cancelada. Puedes volver a intentarlo.',{exact:true}).waitFor();assert.equal(await b('EVALUAR FOTO CON IA').isEnabled(),true);assert.equal(await page.locator('.bag-diagram').count(),1);await b('CERRAR').click();
console.log('PASS: no automatic uploads, chosen-photo-only payload, real response validation, two distinct tips stable across reopen/language, no-score response, quota, invalid response, cancellation, local report unaffected. Service mocked only in this test.');
}finally{await browser.close();}
