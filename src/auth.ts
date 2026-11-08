export function validation(username:string,password:string,register:boolean,confirmation=''):string|null {
  if(username.trim().length<3)return 'El usuario debe tener al menos 3 caracteres.';
  if(username.trim().length>24)return 'El usuario no puede superar 24 caracteres.';
  if(register){
    if(!password)return 'La contraseña no puede estar vacía.';
    if(password.length>45)return 'La contraseña no puede superar 45 caracteres.';
    if(!/^[\p{L}\p{Nd}]+$/u.test(password))return 'Usa solo letras y números en la contraseña.';
    if(password!==confirmation)return 'Las contraseñas no coinciden.';
  }else if(!password)return 'Ingresa tu contraseña.';
  return null;
}
type Account = {username:string;hash:string;salt:string};
function db():Promise<IDBDatabase> {return new Promise((resolve,reject)=>{const r=indexedDB.open('packguide-local',1);r.onupgradeneeded=()=>r.result.createObjectStore('accounts',{keyPath:'username'});r.onsuccess=()=>resolve(r.result);r.onerror=()=>reject(r.error);});}
const base64=(data:Uint8Array)=>btoa(String.fromCharCode(...data));
async function derive(password:string,salt:Uint8Array<ArrayBuffer>) {
  const key=await crypto.subtle.importKey('raw',new TextEncoder().encode(password),'PBKDF2',false,['deriveBits']);
  return base64(new Uint8Array(await crypto.subtle.deriveBits({name:'PBKDF2',salt,iterations:120000,hash:'SHA-256'},key,256)));
}
export async function authenticate(username:string,password:string,register:boolean):Promise<string|null> {
 let database:IDBDatabase|undefined;
 try {
  database=await db(); const normalized=username.trim().toLowerCase();
  const existing=await new Promise<Account|undefined>((resolve,reject)=>{const r=database!.transaction('accounts').objectStore('accounts').get(normalized);r.onsuccess=()=>resolve(r.result);r.onerror=()=>reject(r.error);});
  if(register){
   if(existing)return 'Ese usuario ya existe. Inicia sesión con esa cuenta.';
   const salt=crypto.getRandomValues(new Uint8Array(16));const hash=await derive(password,salt);
   await new Promise<void>((resolve,reject)=>{const tx=database!.transaction('accounts','readwrite');tx.objectStore('accounts').add({username:normalized,salt:base64(salt),hash});tx.oncomplete=()=>resolve();tx.onerror=()=>reject(tx.error);});
  }else{
   if(!existing)return 'Usuario o contraseña incorrectos.';
   const actual=await derive(password,Uint8Array.from(atob(existing.salt),c=>c.charCodeAt(0)));
   let diff=actual.length^existing.hash.length;for(let i=0;i<actual.length;i++)diff|=actual.charCodeAt(i)^(existing.hash.charCodeAt(i)||0);
   if(diff)return 'Usuario o contraseña incorrectos.';
  }
  return null;
 }catch{return 'No se pudo acceder a la base local. Intenta de nuevo.';}finally{database?.close();}
}
