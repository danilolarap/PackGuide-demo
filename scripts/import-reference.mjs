import fs from 'node:fs';
const dir = 'reference/';
const read = suffix => fs.readFileSync(dir + fs.readdirSync(dir).find(f=>f.endsWith(suffix)), 'utf8');
const catalog = [...read('__PackingState.kt').matchAll(/CatalogItemData\("([^"]+)", "([^"]+)", "([^"]+)", ItemDimensions\((\d+), (\d+), (\d+)\), ([\d.]+), Icons\.(?:AutoMirrored\.)?Outlined\.(\w+), (\d+)\)/g)].map(m=>({id:m[1],name:m[2],category:m[3],dimensions:[+m[4],+m[5],+m[6]],unitVolumeL:+m[7],icon:m[8].replace(/[A-Z]/g,(x,i)=>(i?'_':'')+x.toLowerCase()),quantity:+m[9]}));
if(catalog.length!==36)throw new Error('Catalog extraction failed');
fs.writeFileSync('src/catalog.json', JSON.stringify(catalog,null,2)+'\n');
const translations = {};
for(const m of read('__AppText.kt').matchAll(/("(?:[^"\\]|\\.)*")\s+to\s+("(?:[^"\\]|\\.)*")/g)) {
  try {translations[JSON.parse(m[1].replaceAll('\\$','$'))]=JSON.parse(m[2].replaceAll('\\$','$'));}catch{}
}
for(const m of read('__PackingPreviewPanels.kt').matchAll(/previewText\("([^"]*)",\s*"([^"]*)"\)/g))translations[m[1]]=m[2];
fs.writeFileSync('src/translations.json',JSON.stringify(translations,null,2)+'\n');
const common = [...read('__AppText.kt').split('private val commonTermTranslations')[1].matchAll(/"([^"]*)"\s+to\s+"([^"]*)"/g)].map(m=>[m[1],m[2]]).sort((a,b)=>b[0].length-a[0].length);
fs.writeFileSync('src/common-terms.json',JSON.stringify(common,null,2)+'\n');
console.log(`Imported ${catalog.length} items and ${Object.keys(translations).length} translations.`);
