function cover(title, colorA, colorB) {
  const safeTitle = String(title)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')

  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" width="960" height="540" viewBox="0 0 960 540">
      <defs>
        <linearGradient id="g" x1="0" x2="1" y1="0" y2="1">
          <stop stop-color="${colorA}" />
          <stop offset="1" stop-color="${colorB}" />
        </linearGradient>
      </defs>
      <rect width="960" height="540" fill="url(#g)" />
      <circle cx="774" cy="132" r="96" fill="rgba(255,255,255,0.18)" />
      <circle cx="136" cy="430" r="136" fill="rgba(255,255,255,0.14)" />
      <text x="72" y="260" fill="white" font-size="62" font-family="Arial, sans-serif" font-weight="700">${safeTitle}</text>
      <text x="72" y="330" fill="rgba(255,255,255,0.82)" font-size="26" font-family="Arial, sans-serif">BilBil demo video</text>
    </svg>
  `.trim()

  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

export const mockVideos = [
  {
    videoId: 'BV1001',
    videoName: '夏日游戏混剪：高燃名场面合集',
    videoCover: cover('GAME MIX', '#00A1D6', '#FB7299'),
    videoUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
    userName: 'MachU',
    playCount: 128000,
    likeCount: 9300,
    coinCount: 1600,
    collectCount: 4200,
    danmuCount: 3420,
    duration: '12:48'
  },
  {
    videoId: 'BV1002',
    videoName: '从零开始写一个弹幕播放器',
    videoCover: cover('DANMU DEV', '#00A1D6', '#6C7BFF'),
    videoUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
    userName: '前端研究所',
    playCount: 86000,
    likeCount: 5100,
    coinCount: 880,
    collectCount: 2300,
    danmuCount: 1180,
    duration: '18:22'
  },
  {
    videoId: 'BV1003',
    videoName: '一天吃遍城市里的宝藏小店',
    videoCover: cover('CITY FOOD', '#FB7299', '#FFB86C'),
    videoUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
    userName: '生活观察员',
    playCount: 214000,
    likeCount: 18800,
    coinCount: 3900,
    collectCount: 7600,
    danmuCount: 5680,
    duration: '09:37'
  },
  {
    videoId: 'BV1004',
    videoName: '深夜歌单现场：霓虹灯下的温柔节拍',
    videoCover: cover('NIGHT SET', '#2D3A8C', '#FB7299'),
    videoUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
    userName: 'Loop Room 音乐室',
    playCount: 97000,
    likeCount: 7200,
    coinCount: 1100,
    collectCount: 3100,
    danmuCount: 1490,
    duration: '24:05'
  }
]
