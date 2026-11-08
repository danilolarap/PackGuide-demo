import {liters,metrics} from './model';
import type {Bag} from './model';
import {translate} from './i18n';
import type {Language} from './i18n';
export type Photo={id:string;url:string;width:number;height:number};
export async function readPhoto(file:File,signal:AbortSignal):Promise<Photo> {
 if(!['image/jpeg','image/png','image/webp'].includes(file.type))throw new Error('Ese archivo no es una foto compatible. Elige una imagen JPG, PNG o WebP.');
 if(file.size>50*1024*1024)throw new Error('La foto es demasiado grande. Elige una imagen más pequeña.');
 const bitmap=await createImageBitmap(file,{imageOrientation:'from-image'}).catch(()=>{throw new Error('Ese archivo no es una foto compatible. Elige una imagen JPG, PNG o WebP.');});
 try {
  signal.throwIfAborted();
  if(bitmap.width*bitmap.height>100_000_000)throw new Error('La foto es demasiado grande. Elige una imagen más pequeña.');
  let sample=1;while(Math.max(bitmap.width,bitmap.height)/sample>1600)sample*=2;
  const scale=1/sample;
  const canvas=document.createElement('canvas');canvas.width=Math.round(bitmap.width*scale);canvas.height=Math.round(bitmap.height*scale);canvas.getContext('2d')!.drawImage(bitmap,0,0,canvas.width,canvas.height);
  const blob=await new Promise<Blob>((resolve,reject)=>canvas.toBlob(b=>b?resolve(b):reject(new Error('Photo encoding failed')),'image/png'));
  signal.throwIfAborted();return {id:crypto.randomUUID(),url:URL.createObjectURL(blob),width:canvas.width,height:canvas.height};
 }finally{bitmap.close();}
}
export async function compose(bag:Bag,photo:Photo,lang:Language,signal:AbortSignal):Promise<string> {
 const items=bag.items.filter(i=>i.quantity>0);const zoneList=['ACCESO RÁPIDO','CENTRO','BASE'];
 if(!items.length||items.length>64)throw new Error('Invalid packing plan');
 const zoneItems=zoneList.map(z=>({label:z,items:items.filter(i=>i.zone===z)}));
 const height=750+zoneItems.reduce((n,z)=>n+56+(z.items.length?Math.ceil(z.items.length/2)*112+8:62),0);
 if(height>4096)throw new Error('Invalid layout height');
 const canvas=document.createElement('canvas');canvas.width=1200;canvas.height=height;const c=canvas.getContext('2d')!;
 const ink='#0F0F11',muted='#5E646E',red='#D22020';const choose=(es:string,en:string)=>lang==='en'?en:es;
 const text=(s:string,x:number,y:number,size=28,color=ink)=>{c.fillStyle=color;c.font=`${size}px Roboto, sans-serif`;c.fillText(s,x,y);};
 const box=(x:number,y:number,w:number,h:number,fill='#FFFFFF',stroke='#D1D5DB',r=12)=>{c.beginPath();c.roundRect(x,y,w,h,r);c.fillStyle=fill;c.fill();c.lineWidth=2;c.strokeStyle=stroke;c.stroke();};
 const fit=(s:string,x:number,y:number,w:number,size:number,color=ink)=>{c.font=`${size}px Roboto, sans-serif`;let value=s;while(value.length&&c.measureText(value+(value===s?'':'…')).width>w)value=value.slice(0,-1);text(value+(value===s?'':'…'),x,y,size,color);};
 c.fillStyle='#F2F4F7';c.fillRect(0,0,1200,height);
 text(choose('VISTA ORGANIZADA DE EMPAQUE','ORGANIZED PACKING PREVIEW'),48,65,36);
 text(choose('Composición local · foto original + inventario','Local composition · original photo + inventory'),48,105,26,muted);
 box(48,140,552,330);const image=new Image();image.src=photo.url;await image.decode();signal.throwIfAborted();
 const scale=Math.min(528/photo.width,306/photo.height),w=photo.width*scale,h=photo.height*scale;c.drawImage(image,324-w/2,305-h/2,w,h);
 const m=metrics(bag);text(choose('FOTO DE REFERENCIA','REFERENCE PHOTO'),48,505,24,muted);
 text(choose('INVENTARIO SELECCIONADO','SELECTED INVENTORY'),640,180,26);
 text(`${m.selected} `+choose('unidades','units'),640,230,34);
 text(`${liters(m.used)} L / ${liters(m.capacity)} L`,640,285,30);
 text(choose('Estimación / capacidad interior','Estimate / interior capacity'),640,325,24,muted);
 text((m.over?choose('EXCESO EST.: ','EST. EXCESS: '):choose('ESPACIO EST.: ','EST. FREE SPACE: '))+liters(m.over?m.excess:m.free)+' L',640,380,27,m.over?red:ink);
 text(bag.dimensions.join(' × ')+' cm',640,430,27,muted);box(480,535,240,40,'#FFFFFF',ink);box(48,565,1104,height-665,'#FFFFFF',ink,28);
 let y=610;
 for(const zone of zoneItems){
  signal.throwIfAborted();text(translate(zone.label,lang),80,y,30);y+=22;
  if(!zone.items.length){text(choose('Sin artículos asignados','No assigned items'),90,y+30,25,muted);y+=62;}
  else {zone.items.forEach((item,index)=>{
   const x=index%2===0?80:610,rowY=y+Math.floor(index/2)*112;box(x,rowY,510,98,'#F5F7F9');c.strokeStyle=ink;c.lineWidth=3;
   const line=(x1:number,y1:number,x2:number,y2:number)=>{c.beginPath();c.moveTo(x+x1,rowY+y1);c.lineTo(x+x2,rowY+y2);c.stroke();};
   const outline=(a:number,b:number,w:number,h:number,r=5)=>{c.beginPath();c.roundRect(x+a,rowY+b,w,h,r);c.stroke();};
   if(item.category==='CALZADO'){outline(16,48,54,24,8);line(16,48,24,28);line(24,28,42,48);}
   else if(item.category==='ASEO'){outline(25,32,35,44);outline(33,20,19,12,0);}
   else if(item.category==='TECH'){outline(24,22,38,54);line(32,65,54,65);}
   else{outline(18,30,51,40,8);line(18,49,69,49);}
   fit(`${item.quantity} × ${translate(item.name,lang)}`,x+88,rowY+38,405,26);fit(item.dimensions.join(' × ')+' cm',x+88,rowY+73,405,23,muted);
  });y+=Math.ceil(zone.items.length/2)*112+8;}
  y+=34;await new Promise(r=>setTimeout(r,0));
 }
 text(choose('Esquema orientativo; no mide ni garantiza el ajuste físico.','Illustrative layout; it does not measure or guarantee physical fit.'),48,height-50,26,muted);
 signal.throwIfAborted();const blob=await new Promise<Blob>((resolve,reject)=>canvas.toBlob(b=>b?resolve(b):reject(new Error('Composition failed'))));signal.throwIfAborted();return URL.createObjectURL(blob);
}
