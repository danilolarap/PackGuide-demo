import {useEffect,useRef,useState} from 'react';
import {Button,Icon,Modal,TextButton} from './components';
import {useCopy} from './i18n';
import {metrics} from './model';
import type {Bag} from './model';
import type {Photo} from './composition';
import {bagMotif,TravelIcon} from './TravelMotifs';
import {packingTips,pickTips} from './packingTips';

type Assessment={score:number|null;feedback:string};
type Review={assessment:Assessment;tips:[number,number]};
// Weak keys keep results stable while a photo belongs to this session, without retaining removed photos.
const reviews=new WeakMap<Photo,Partial<Record<'es'|'en',Review>>>();
const tipPairs=new WeakMap<Photo,[number,number]>();
const reviewEndpoint=import.meta.env.VITE_PACKGUIDE_REVIEW_ENDPOINT as string|undefined;
async function assessPhoto(photo:Photo,language:'es'|'en',signal:AbortSignal):Promise<Assessment>{
 const image=new Image();image.src=photo.url;await image.decode();signal.throwIfAborted();
 const canvas=document.createElement('canvas');const scale=Math.min(1,1200/Math.max(image.width,image.height));canvas.width=Math.round(image.width*scale);canvas.height=Math.round(image.height*scale);canvas.getContext('2d')!.drawImage(image,0,0,canvas.width,canvas.height);
 const photoBase64=canvas.toDataURL('image/jpeg',.8).split(',')[1];
 const response=await fetch(reviewEndpoint!,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({schemaVersion:1,language,photoBase64}),signal});
 if(!response.ok)throw new Error(response.status===429?'quota':'connection');const result=await response.json() as {score?:unknown;feedback?:unknown};
 if((result.score!==null&&(!Number.isInteger(result.score)||Number(result.score)<1||Number(result.score)>10))||typeof result.feedback!=='string'||!result.feedback.trim())throw new Error('response');
 return {score:result.score as number|null,feedback:result.feedback};
}
function BagDiagram({bag}:{bag:Bag}){
 const {t,choose}=useCopy(),m=metrics(bag);
 const active=m.selected?Math.round(m.packed/m.selected*40):0;
 return <article className={`bag-diagram bag-${bag.type.toLowerCase()}`} data-bag={bag.type}>
  <div className="diagram-emblem"><TravelIcon kind={bagMotif[bag.type]} size={bag.type==='BODEGA'?54:42} red/></div><div className="organized-handle"/>
  <div className="organized-suitcase"><header className="diagram-header"><TravelIcon kind={bagMotif[bag.type]} size={32} red/><div><p className="label-small">{choose('DISTRIBUCIÓN POR ZONAS','LAYOUT BY ZONE')}</p><h3>{t(bag.type)}</h3><p>{bag.dimensions.join(' × ')} cm</p></div></header>
  <p className="packing-count">{m.packed}/{m.selected} {choose('unidades empacadas','units packed')} · {m.progress}%</p>
  <div className="dot-progress" role="progressbar" aria-label={choose('Progreso de empaque','Packing progress')} aria-valuenow={m.progress} aria-valuemin={0} aria-valuemax={100}>{Array.from({length:40},(_,i)=><i key={i} className={i<active?'filled':''}/>)}</div>
  {(['ACCESO RÁPIDO','CENTRO','BASE'] as const).map((zone,index)=>{const assigned=bag.items.filter(i=>i.quantity>0&&i.zone===zone);return <section key={zone}><div className="zone-title"><span>{String(index+1).padStart(2,'0')}</span><h3>{t(zone)}</h3><small>{assigned.reduce((n,i)=>n+i.quantity,0)} {choose('unidades','units')}</small></div>{assigned.length?<div className="organized-tiles">{assigned.map(i=><div className="organized-tile" key={i.id}><Icon name={i.icon} size={22}/><div><strong className="tile-quantity">×{i.quantity}</strong><p>{t(i.name)}</p><small>{i.dimensions.join(' × ')} cm</small></div></div>)}</div>:<p className="organized-empty">{choose('Sin artículos asignados','No assigned items')}</p>}</section>;})}
  {bag.type==='MANO'&&<div className="backpack-pocket" aria-hidden="true"><span>••••••••••</span></div>}</div>{bag.type!=='MANO'&&<div className="bag-wheels" aria-hidden="true"><i/><i/></div>}
 </article>;
}
export function OrganizedPreview({bag,bags,photo,onClose}:{bag:Bag;bags:Bag[];photo?:Photo;onClose:()=>void}){
 const {t,choose,lang,volume,balance}=useCopy();const m=metrics(bag);
 const [review,setReview]=useState<Review|undefined>(()=>photo?(reviews.get(photo)?.[lang]??reviews.get(photo)?.es??reviews.get(photo)?.en):undefined),[working,setWorking]=useState(false),[error,setError]=useState<string|null>(null),request=useRef<AbortController|null>(null);
 useEffect(()=>{request.current?.abort();request.current=null;setWorking(false);setError(null);setReview(photo?(reviews.get(photo)?.[lang]??reviews.get(photo)?.es??reviews.get(photo)?.en):undefined);},[photo,lang]);
 useEffect(()=>()=>request.current?.abort(),[]);
 async function evaluate(){if(!photo||!reviewEndpoint||request.current)return;const controller=new AbortController();request.current=controller;setWorking(true);setError(null);try{const assessment=await assessPhoto(photo,lang,controller.signal);controller.signal.throwIfAborted();const tips=tipPairs.get(photo)??pickTips();if(assessment.score!==null)tipPairs.set(photo,tips);const result={assessment,tips};reviews.set(photo,{...reviews.get(photo),[lang]:result});setReview(result);}catch(e){if(!controller.signal.aborted)setError(e instanceof Error?e.message:'connection');}finally{if(request.current===controller){request.current=null;setWorking(false);}}}
 const assessment=review?.assessment;
 const status=working?choose('La IA está evaluando la foto…','AI is assessing the photo…'):!photo?choose('Añade una foto de tu maleta para recibir una puntuación y sugerencias.','Add a photo of your bag to receive a score and suggestions.'):!reviewEndpoint?choose('La evaluación no está configurada en esta versión de la aplicación.','Assessment is not configured in this version of the application.'):choose('Foto lista para evaluar. Pulsa EVALUAR FOTO CON IA para obtener tu puntuación.','Photo ready to assess. Press ASSESS PHOTO WITH AI to get your score.');
 return <Modal className="organized-dialog" label={choose('Vista previa de organización','Organized packing preview')} onClose={onClose}>
 <header className="organized-top"><TravelIcon kind="plane" size={28} red/><span>PACKGUIDE / {choose('INFORME DE EQUIPAJE','LUGGAGE REPORT')}</span></header>
 <div className="organized-scroll"><h2>{choose('VISTA PREVIA DE ORGANIZACIÓN','ORGANIZED PACKING PREVIEW')}</h2><p className="organized-subtitle">{choose('Composición local · foto original e inventario','Local composition · original photo and inventory')}</p>
 <div className="organized-overview"><div><div className="organized-photo">{photo?<img src={photo.url} alt={choose('Foto de referencia','Reference photo')}/>:<span>{choose('Sin foto de referencia','No reference photo')}</span>}</div><p className="organized-caption">{choose('FOTO DE REFERENCIA','REFERENCE PHOTO')}</p></div><div className="organized-stats"><h3>{t(bag.type)}</h3><p>{choose(`${m.selected} unidades seleccionadas`,`${m.selected} selected units`)}</p><p>{volume(bag)}</p><small>{choose('Estimación / capacidad interior','Estimate / interior capacity')}</small><p className={m.over?'red':''}>{balance(bag)}</p><p>{bag.dimensions.join(' × ')} cm</p></div></div>
 <section className="organized-advice" aria-live="polite"><h3>{choose('PUNTUACIÓN DE LA IA','AI SCORE')}{assessment?.score!=null?` · ${assessment.score}/10`:''}</h3><p>{working?status:assessment?.feedback??status}</p>{photo&&<p className="organized-hint">{choose('Al evaluar, se enviará solo la foto que elijas al servicio de IA.','Only the photo you select will be sent to the AI service for assessment.')}</p>}<Button disabled={!photo||!reviewEndpoint||working} onClick={()=>void evaluate()}>{choose('EVALUAR FOTO CON IA','ASSESS PHOTO WITH AI')}</Button>{working&&<TextButton onClick={()=>{request.current?.abort();request.current=null;setWorking(false);setError('cancelled');}}>{t('CANCELAR')}</TextButton>}{error&&<p role="status" className="red">{error==='cancelled'?choose('Evaluación cancelada. Puedes volver a intentarlo.','Assessment cancelled. You can try again.'):error==='quota'?choose('Cuota de evaluación agotada. Intenta más tarde.','Assessment quota exhausted. Try later.'):error==='response'?choose('La respuesta no es válida. Intenta evaluar de nuevo.','The response is invalid. Try assessing again.'):choose('No se pudo conectar con el servicio. La vista local sigue disponible.','Could not connect to the service. The local preview remains available.')}</p>}</section>
 {review&&assessment?.score!=null&&<section className="organized-advice general-tips"><h3>{choose('CONSEJOS PARA ORGANIZAR TU MALETA','TIPS FOR ORGANIZING YOUR BAG')}</h3><p className="organized-hint">{choose('Consejos generales · independientes del comentario sobre la foto','General tips · separate from the photo feedback')}</p><ol>{review.tips.map(i=><li key={i}>{packingTips[i][lang==='es'?0:1]}</li>)}</ol></section>}
 <h3 className="trip-report-heading">{choose('EQUIPAJE DEL VIAJE','TRIP LUGGAGE')}</h3><div className="trip-diagrams">{bags.map(b=><BagDiagram key={b.type} bag={b}/>)}</div><p className="organized-footnote">{choose('Distribución ilustrativa. El esquema no mide ni garantiza el ajuste físico.','Illustrative layout. The diagram does not measure or guarantee physical fit.')}</p></div>
 <footer className="organized-footer"><Button onClick={onClose}>{choose('CERRAR','CLOSE')}</Button></footer></Modal>;
}
