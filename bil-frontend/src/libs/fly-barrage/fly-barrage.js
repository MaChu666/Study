var w = Object.defineProperty;
var I = (n, t, i) => t in n ? w(n, t, { enumerable: !0, configurable: !0, writable: !0, value: i }) : n[t] = i;
var a = (n, t, i) => (I(n, typeof t != "symbol" ? t + "" : t, i), i);
const S = () => {
  let n = document.createElement("canvas"), t = n.getContext("2d"), i = function() {
    let e = window.devicePixelRatio || 1, s = t.webkitBackingStorePixelRatio || t.mozBackingStorePixelRatio || t.msBackingStorePixelRatio || t.oBackingStorePixelRatio || t.backingStorePixelRatio || 1;
    return e / s;
  }();
  return n.width = n.height = 0, i;
}, b = {
  getDevicePixelRatio: S
}, O = (n) => {
  let t = 0;
  return n.forEach((i) => t += i), t;
}, L = (n) => {
  if (n.length === 0)
    return null;
  let t = n.reduce((s, r) => ({ ...s, [r]: (s[r] || 0) + 1 }), {}), i = 0, e = null;
  for (const s in t)
    t[s] > i && (i = t[s], e = parseInt(s));
  return e;
}, k = (n, t) => (n = Math.ceil(n), t = Math.floor(t), Math.floor(Math.random() * (t - n + 1)) + n), E = {
  sum: O,
  findMode: L,
  getRandomInt: k
}, u = {}, F = (n) => (u[n] || (u[n] = document.createElement("img"), u[n].src = n), u[n]), C = {}, M = (n) => new Promise((t, i) => {
  if (C[n])
    t(C[n]);
  else {
    const e = new Image();
    e.src = n, e.onload = () => {
      C[n] = e, t(e);
    }, e.onerror = () => {
      i("load image error");
    };
  }
}), D = {
  imageElementFactory: F,
  loadImage: M
}, H = (n, t) => {
  let i = 0, e = n.length - 1;
  for (; i <= e; ) {
    let s = Math.floor((i + e) / 2);
    n[s].time < t.time ? i = s + 1 : e = s - 1;
  }
  n.splice(i, 0, t);
}, _ = {
  insertBarrageByTime: H
};
function A(n) {
  if (!n)
    return !1;
  const t = n.trim().toLowerCase();
  if (t === "transparent")
    return !1;
  const i = n.match(/^rgba\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*,\s*([\d.]+)\s*\)$/i);
  if (i) {
    const l = parseInt(i[1], 10), c = parseInt(i[2], 10), d = parseInt(i[3], 10);
    if (l >= 0 && l <= 255 && c >= 0 && c <= 255 && d >= 0 && d <= 255)
      return parseFloat(i[4]) > 0;
  }
  const e = n.match(/^hsla\(\s*(\d+)\s*,\s*(\d+)%\s*,\s*(\d+)%\s*,\s*([\d.]+)\s*\)$/i);
  if (e) {
    const l = parseInt(e[1], 10), c = parseInt(e[2], 10), d = parseInt(e[3], 10);
    if (l >= 0 && l <= 360 && c >= 0 && c <= 100 && d >= 0 && d <= 100)
      return parseFloat(e[4]) > 0;
  }
  const s = n.match(/^rgb\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)$/i);
  if (s) {
    const l = parseInt(s[1], 10), c = parseInt(s[2], 10), d = parseInt(s[3], 10);
    return l >= 0 && l <= 255 && c >= 0 && c <= 255 && d >= 0 && d <= 255;
  }
  const r = n.match(/^#([0-9a-f]{3}|[0-9a-f]{4}|[0-9a-f]{6}|[0-9a-f]{8})$/i);
  if (r) {
    const l = r[1].toLowerCase();
    let c = "ff";
    return l.length === 4 ? c = l[3] + l[3] : l.length === 8 && (c = l.substring(6, 8)), parseInt(c, 16) / 255 > 0;
  }
  const o = n.match(/^hsl\(\s*(\d+)\s*,\s*(\d+)%\s*,\s*(\d+)%\s*\)$/i);
  if (o) {
    const l = parseInt(o[1], 10), c = parseInt(o[2], 10), d = parseInt(o[3], 10);
    return l >= 0 && l <= 360 && c >= 0 && c <= 100 && d >= 0 && d <= 100;
  }
  return (/* @__PURE__ */ new Set([
    "white",
    "black",
    "red",
    "green",
    "blue",
    "yellow",
    "purple",
    "orange",
    "pink",
    "gray",
    "grey",
    "cyan",
    "magenta"
  ])).has(t);
}
const z = {
  isVisibleColor: A
}, g = {
  Canvas: b,
  Math: E,
  Cache: D,
  Algorithm: _,
  Color: z
};
class v {
  constructor({
    id: t,
    time: i,
    text: e,
    fontSize: s,
    lineHeight: r,
    color: o,
    prior: h = !1,
    customRender: l,
    addition: c
  }, d) {
    // 弹幕的唯一标识
    a(this, "id");
    // 弹幕的出现时间（毫秒为单位）
    a(this, "time");
    // 弹幕的内容
    a(this, "text");
    // 弹幕的字体大小
    a(this, "fontSize");
    // 弹幕的行高
    a(this, "lineHeight");
    // 弹幕颜色
    a(this, "color");
    // 是不是重要的
    a(this, "prior");
    // 自定义 render 相关配置
    a(this, "customRender");
    // 额外，附加的信息
    a(this, "addition");
    // 渲染器实例
    a(this, "br");
    // 用于描述渲染时弹幕整体的 top 和 left
    a(this, "top");
    a(this, "left");
    // 用于描述弹幕整体的尺寸
    a(this, "width");
    a(this, "height");
    // 根据 text 解析成的片段数组
    a(this, "sections", []);
    this.id = t, this.time = i, this.text = e, this.fontSize = s, this.lineHeight = r, this.color = o, this.prior = h, this.customRender = l, this.addition = c, this.br = d, this.initBarrage();
  }
  /**
   * 进行当前弹幕相关数据的计算
   */
  initBarrage() {
    var o, h;
    const t = this.analyseText(this.text);
    let i, e = 0, s = 0;
    const r = [];
    t.forEach((l) => {
      var c, d;
      if (l.type === "image" && (i = (c = this.br.barrageImages) == null ? void 0 : c.find((f) => `[${f.id}]` === l.value)))
        e += i.width, s = s < i.height ? i.height : s, r.push(new N({
          ...i,
          leftOffset: g.Math.sum(r.map((f) => f.width))
        }));
      else {
        this.setCtxFont(this.br.ctx);
        const f = ((d = this.br.ctx) == null ? void 0 : d.measureText(l.value).width) || 0, R = this.fontSize * this.lineHeight;
        e += f, s = s < R ? R : s, r.push(new P({
          text: l.value,
          width: f,
          height: R,
          leftOffset: g.Math.sum(r.map((T) => T.width))
        }));
      }
    }), this.sections = r, this.width = ((o = this.customRender) == null ? void 0 : o.width) ?? e, this.height = ((h = this.customRender) == null ? void 0 : h.height) ?? s, this.sections.forEach((l) => {
      l.sectionType === "text" ? l.topOffset = (this.height - this.fontSize) / 2 : l.topOffset = (this.height - l.height) / 2;
    });
  }
  /**
   * 解析 text 内容
   * 文本内容[图片id]文本内容[图片id] => ['文本内容', '[图片id]', '文本内容', '[图片id]']
   * @param barrageText 弹幕文本
   */
  analyseText(t) {
    const i = [];
    for (; t; ) {
      const r = t.indexOf("]");
      if (r !== -1) {
        const o = t.lastIndexOf("[", r);
        o !== -1 ? (o !== 0 && i.push({
          type: "text",
          value: t.slice(0, o)
        }), i.push({
          type: r - o > 1 ? "image" : "text",
          value: t.slice(o, r + 1)
        }), t = t.slice(r + 1)) : (i.push({
          type: "text",
          value: t.slice(0, r + 1)
        }), t = t.slice(r + 1));
      } else
        i.push({
          type: "text",
          value: t
        }), t = "";
    }
    const e = [];
    let s = "";
    for (let r = 0; r < i.length; r++)
      i[r].type === "text" ? s += i[r].value : (s !== "" && (e.push({ type: "text", value: s }), s = ""), e.push(i[r]));
    return s !== "" && e.push({ type: "text", value: s }), e;
  }
  /**
   * 将当前弹幕渲染到指定的上下文
   * @param ctx 渲染上下文
   */
  render(t) {
    if (t.beginPath(), this.br.beforeBarrageRender && this.br.beforeBarrageRender({
      ctx: t,
      br: this.br,
      barrage: this
    }), this.customRender) {
      this.customRender.renderFn({
        ctx: t,
        barrage: this,
        br: this.br,
        imageElementFactory: g.Cache.imageElementFactory
      });
      return;
    }
    this.br.devConfig.isRenderBarrageBorder && (t.strokeStyle = "#FF0000", t.strokeRect(this.left, this.top, this.width, this.height)), this.prior && (this.br.renderConfig.priorBorderCustomRender ? this.br.renderConfig.priorBorderCustomRender({
      ctx: t,
      barrage: this,
      br: this.br,
      imageElementFactory: g.Cache.imageElementFactory
    }) : (t.strokeStyle = "#89D5FF", t.strokeRect(this.left, this.top, this.width, this.height)));
    const i = this.br.preRenderOptimizer.getImageBitmap(this);
    i ? t.drawImage(
      i,
      this.left,
      this.top,
      this.width,
      this.height
    ) : (this.setCtxFont(t), this.sections.forEach((e) => {
      e.sectionType === "text" ? (g.Color.isVisibleColor(this.br.renderConfig.strokeStyle) && this.br.renderConfig.lineWidth > 0 && t.strokeText(e.text, this.left + e.leftOffset, this.top + e.topOffset), t.fillText(e.text, this.left + e.leftOffset, this.top + e.topOffset)) : e.sectionType === "image" && t.drawImage(
        g.Cache.imageElementFactory(e.url),
        this.left + e.leftOffset,
        this.top + e.topOffset,
        e.width,
        e.height
      );
    })), this.br.afterBarrageRender && this.br.afterBarrageRender({
      ctx: t,
      br: this.br,
      barrage: this
    });
  }
  /**
   * 设置文字相关的属性
   * @param ctx 渲染上下文
   */
  setCtxFont(t) {
    t.font = `${this.br.renderConfig.fontWeight} ${this.fontSize}px ${this.br.renderConfig.fontFamily}`, t.fillStyle = this.color;
  }
}
class P {
  constructor({
    text: t,
    width: i,
    height: e,
    leftOffset: s
  }) {
    a(this, "sectionType", "text");
    a(this, "text");
    a(this, "width");
    a(this, "height");
    a(this, "topOffset");
    a(this, "leftOffset");
    this.text = t, this.width = i, this.height = e, this.leftOffset = s;
  }
}
class N {
  constructor({
    id: t,
    url: i,
    width: e,
    height: s,
    leftOffset: r
  }) {
    a(this, "sectionType", "image");
    a(this, "id");
    a(this, "url");
    a(this, "width");
    a(this, "height");
    a(this, "topOffset");
    a(this, "leftOffset");
    this.id = t, this.url = i, this.width = e, this.height = s, this.leftOffset = r;
  }
}
class B extends v {
  constructor(i, e) {
    super(i, e);
    // 弹幕类型
    a(this, "barrageType");
    // 弹幕持续时间
    a(this, "duration");
    // 弹幕结束时间
    a(this, "endTime");
    const { barrageType: s, duration: r } = i;
    this.barrageType = s, this.duration = r, this.endTime = this.time + r, this.calcFixedBarrageLeft();
  }
  /**
   * 计算固定弹幕的 left 属性
   */
  calcFixedBarrageLeft() {
    this.left = (this.br.canvasSize.width - this.width) / 2;
  }
}
class x extends v {
  constructor(i, e) {
    super(i, e);
    a(this, "barrageType", "scroll");
    // 弹幕持续时间
    a(this, "duration");
    // 用于描述滚动弹幕在播放进度为 0 时，滚动弹幕左侧距离 Canvas 左侧的距离
    a(this, "originalLeft");
    // 用于描述滚动弹幕在播放进度为 0 时，滚动弹幕右侧距离 Canvas 左侧的距离
    a(this, "originalRight");
    // 标识当前的滚动弹幕是否应该显示，当设置不允许遮挡的话，部分滚动弹幕会不显示
    a(this, "show", !0);
    // 当前弹幕会占据几个实际轨道
    a(this, "grade");
    // 弹幕结束渲染的时间点（毫秒为单位）
    a(this, "endTime");
    this.duration = i.duration;
    this.calcOriginal();
  }
  /**
   * 计算原始的 left 和 right 位置
   */
  calcOriginal() {
    if (this.duration > 0) {
      this.barrageSpeed = (this.br.canvasSize.width + this.width) / this.duration * 1e3;
      this.originalLeft = this.br.canvasSize.width + this.time / 1e3 * this.barrageSpeed;
      this.originalRight = this.originalLeft + this.width;
      this.endTime = this.time + this.duration;
    } else {
      this.originalLeft = this.br.canvasSize.width + this.time / 1e3 * this.br.renderConfig.speed, this.originalRight = this.originalLeft + this.width, this.endTime = this.time + (this.br.canvasSize.width + this.width) / this.br.speedPerMs;
    }
  }
}
class y extends v {
  constructor(i, e) {
    super(i, e);
    a(this, "barrageType", "senior");
    // 高级弹幕配置
    a(this, "seniorBarrageConfig");
    // 用于描述高级弹幕在 x、y 轴上的运动速度
    a(this, "vx");
    a(this, "vy");
    // 实际的 起始点 和 结束点
    a(this, "actualStartLocation");
    a(this, "actualEndLocation");
    this.seniorBarrageConfig = i.seniorBarrageConfig, this.calcActualLocation();
  }
  /**
   * 计算关键点的实际坐标
   */
  calcActualLocation() {
    const { startLocation: i, endLocation: e, motionDuration: s } = this.seniorBarrageConfig;
    let r = (i.type || "PIXEL") === "PIXEL" ? i.x : i.x * this.canvasSize.width, o = (i.type || "PIXEL") === "PIXEL" ? i.y : i.y * this.canvasSize.height;
    i.offsetX && (r += i.offsetX), i.offsetY && (o += i.offsetY), this.actualStartLocation = {
      x: r,
      y: o
    };
    let h = (e.type || "PIXEL") === "PIXEL" ? e.x : e.x * this.canvasSize.width, l = (e.type || "PIXEL") === "PIXEL" ? e.y : e.y * this.canvasSize.height;
    e.offsetX && (h += e.offsetX), e.offsetY && (l += e.offsetY), this.actualEndLocation = {
      x: h,
      y: l
    }, this.vx = (this.actualEndLocation.x - this.actualStartLocation.x) / s, this.vy = (this.actualEndLocation.y - this.actualStartLocation.y) / s;
  }
  get canvasSize() {
    return this.br.canvasSize;
  }
}
class W {
  constructor(t) {
    a(this, "br");
    // 用于维护当前渲染的顶部弹幕（从上往下排序）
    a(this, "topRenderBarrages", []);
    // 用于维护当前渲染的底部弹幕（从下往上排序）
    a(this, "bottomRenderBarrages", []);
    this.br = t;
  }
  /**
   * 获取当前应该渲染的固定弹幕
   * @param allFixedBarrages 所有的固定弹幕数组
   * @param time 视频播放的时间点
   */
  getRenderFixedBarrages(t, i) {
    const e = t.filter((h) => h.barrageType === "top" && i >= h.time && i <= h.endTime), s = t.filter((h) => h.barrageType === "bottom" && i >= h.time && i <= h.endTime);
    this.topRenderBarrages = this.topRenderBarrages.filter((h) => e.includes(h)), this.bottomRenderBarrages = this.bottomRenderBarrages.filter((h) => s.includes(h));
    const r = e.filter((h) => !this.topRenderBarrages.includes(h)), o = s.filter((h) => !this.bottomRenderBarrages.includes(h));
    return r.forEach((h) => {
      this.insertFixedBarrage(h);
    }), o.forEach((h) => {
      this.insertFixedBarrage(h);
    }), [...this.topRenderBarrages, ...this.bottomRenderBarrages];
  }
  /**
   * 发送新的弹幕
   * @param barrage 弹幕实例
   */
  send(t) {
    this.insertFixedBarrage(t);
  }
  /**
   * 封装通用的工具方法
   * @param barrage 弹幕实例
   */
  insertFixedBarrage(t) {
    let i = !1;
    if (t.barrageType === "top")
      if (this.topRenderBarrages.length === 0)
        this.topRangeLength >= t.height && (t.top = this.topRange[0], this.topRenderBarrages.push(t), i = !0);
      else
        for (let e = 0; e < this.topRenderBarrages.length; e++) {
          const s = this.topRenderBarrages[e];
          if (e === 0 && s.top - this.topRange[0] >= t.height) {
            t.top = this.topRange[0], this.topRenderBarrages.unshift(t), i = !0;
            break;
          }
          if ((e === this.topRenderBarrages.length - 1 ? this.topRange[1] - s.top - s.height : this.topRenderBarrages[e + 1].top - s.top - s.height) >= t.height) {
            t.top = s.top + s.height, this.topRenderBarrages.splice(e + 1, 0, t), i = !0;
            break;
          }
        }
    else if (this.bottomRenderBarrages.length === 0)
      this.bottomRangeLength >= t.height && (t.top = this.bottomRange[0] - t.height, this.bottomRenderBarrages.push(t), i = !0);
    else
      for (let e = 0; e < this.bottomRenderBarrages.length; e++) {
        const s = this.bottomRenderBarrages[e];
        if (e === 0 && this.bottomRange[0] - s.top - s.height >= t.height) {
          t.top = this.bottomRange[0] - t.height, this.bottomRenderBarrages.unshift(t), i = !0;
          break;
        }
        if ((e === this.bottomRenderBarrages.length - 1 ? s.top - this.bottomRange[1] : s.top - this.bottomRenderBarrages[e + 1].top - this.bottomRenderBarrages[e + 1].height) >= t.height) {
          t.top = s.top - t.height, this.bottomRenderBarrages.splice(e + 1, 0, t), i = !0;
          break;
        }
      }
    t.prior && !i && (t.barrageType === "top" ? (t.top = g.Math.getRandomInt(this.topRange[0], this.topRange[1] - t.height), this.topRenderBarrages.push(t), this.topRenderBarrages.sort((e, s) => e.top - s.top)) : (t.top = g.Math.getRandomInt(this.bottomRange[1], this.bottomRange[0] - t.height), this.bottomRenderBarrages.push(t), this.bottomRenderBarrages.sort((e, s) => s.top - e.top)));
  }
  /**
   * 清空缓存数组
   */
  clearStoredBarrage() {
    this.topRenderBarrages = [], this.bottomRenderBarrages = [];
  }
  /**
   * 一半的 Canvas 高度，top 弹幕只能在 halfCanvasHeight 的上面，bottom 弹幕只能在 halfCanvasHeight 的下面
   */
  get middleHeightPoint() {
    return this.br.canvasSize.height / 2;
  }
  /**
   * 顶部弹幕 y 轴方向的范围
   */
  get topRange() {
    return [0, this.middleHeightPoint];
  }
  /**
   * topRange 的长度
   */
  get topRangeLength() {
    return this.topRange[1] - this.topRange[0];
  }
  /**
   * 底部弹幕 y 轴方向的范围
   */
  get bottomRange() {
    return [this.br.canvasSize.height, this.middleHeightPoint];
  }
  /**
   * bottomRange 的长度
   */
  get bottomRangeLength() {
    return this.bottomRange[0] - this.bottomRange[1];
  }
}
class V {
  constructor(t, i) {
    // 全局弹幕渲染器
    a(this, "br");
    // 弹幕布局计算器
    a(this, "bLCalculate");
    // 实际轨道数组
    a(this, "realTracks", []);
    // 虚拟轨道数组
    a(this, "virtualTracks", []);
    // 实际轨道的高度
    a(this, "realTrackHeight");
    // 实际轨道的数量
    a(this, "realTrackNum");
    // 最高弹幕所占虚拟轨道的 grade
    a(this, "maxGrade");
    // 以空间换时间，性能优化
    // key：任意虚拟轨道；value：包含 virtualTrack 内部任一实际轨道的虚拟轨道所组成的数组
    a(this, "vtToVtsMap", /* @__PURE__ */ new Map());
    a(this, "gradeToVtsMap", /* @__PURE__ */ new Map());
    this.br = t, this.bLCalculate = i;
  }
  /**
   * 根据 br 的数据初始化实际轨道和虚拟轨道
   * @param realTrackHeight 实际轨道高度
   */
  initTracks(t) {
    this.resetTracks();
    const i = Math.floor(this.br.canvasSize.height * this.br.renderConfig.renderRegion / t);
    this.realTrackHeight = t, this.realTrackNum = i;
    for (let r = 1; r <= i; r++)
      this.realTracks.push(new X(r, t));
    const e = this.realTracks.map((r) => r.id);
    let s = 1;
    for (let r = 1; r <= i; r++) {
      const o = i - (r - 1);
      for (let h = 1; h <= o; h++)
        this.virtualTracks.push(
          new Y(
            s++,
            e.slice(h - 1, h - 1 + r),
            this.realTracks.slice(h - 1, h - 1 + r)
          )
        );
    }
    this.isLogKeyData && console.table([
      { item: "实际轨道高度", value: t },
      { item: "实际轨道数量", value: this.realTracks.length },
      { item: "虚拟轨道数量", value: this.virtualTracks.length }
    ]), this.virtualTracks.forEach((r) => {
      this.vtToVtsMap.set(r, this.virtualTracks.filter((o) => o.grade <= this.maxGrade && r.rtIdArr.some((h) => o.rtIdSet.has(h))));
    });
    for (let r = 1; r <= i; r++)
      this.gradeToVtsMap.set(r, this.virtualTracks.filter((o) => o.grade === r));
  }
  /**
   * 对滚动弹幕进行布局计算
   * @param scrollBarrages 滚动弹幕实例数组
   */
  layoutScrollBarrages(t) {
    if (t.length === 0)
      return;
    const i = g.Math.findMode(t.map((e) => Math.ceil(e.height)));
    this.maxGrade = Math.ceil(Math.max(...t.map((e) => e.height)) / i), (!this.realTracks.length || !this.virtualTracks.length) && this.initTracks(i), t.forEach((e) => {
      e.grade = Math.ceil(e.height / i);
    }), this.avoidOverlap ? this.avoidOverlapLayout(t) : this.allowOverlapLayout(t), this.bLCalculate.canShowScrollBarrageInstances = this.bLCalculate.scrollBarrageInstances.filter((e) => e.show && e.top !== void 0);
  }
  /**
   * 进行不允许重叠的布局
   * @param scrollBarrages 滚动弹幕实例数组
   */
  avoidOverlapLayout(t) {
    const i = Date.now();
    this.virtualTracks.forEach((e) => e.clearBarrage()), t.forEach((e) => {
      const s = this.gradeToVtsMap.get(e.grade) || [];
      for (let r = 0; r < s.length; r++) {
        const o = s[r];
        if ((this.vtToVtsMap.get(o) || []).every((l) => {
          const c = l.getLastBarrage();
          return c ? c.originalRight + this.minSpace <= e.originalLeft : !0;
        })) {
          e.show = !0, o.push(e), e.top = o.top;
          break;
        } else
          e.show = !1;
      }
      e.prior && !e.show && this.randomTrackBarrage(e);
    }), this.isLogKeyData && console.log(`虚拟轨道算法花费时间：${Date.now() - i}ms`);
  }
  /**
   * 进行允许重叠的布局
   * @param scrollBarrages 滚动弹幕实例数组
   */
  allowOverlapLayout(t) {
    t.forEach((i) => {
      this.randomTrackBarrage(i);
    });
  }
  /**
   * 不允许重叠，插入新的弹幕
   * @param scrollBarrage 滚动弹幕实例
   */
  avoidOverlapInsert(t) {
    let i = !1;
    const e = this.gradeToVtsMap.get(t.grade) || [];
    for (let s = 0; s < e.length; s++) {
      const r = e[s];
      if (r.isEmpty) {
        t.show = !0, r.push(t), t.top = r.top, i = !0;
        break;
      } else if (t.originalLeft < r.getByIndex(0).originalLeft) {
        if (t.originalRight + this.minSpace < r.getByIndex(0).originalLeft) {
          t.show = !0, r.barrages.unshift(t), t.top = r.top, i = !0;
          break;
        }
      } else {
        const o = r.barrages.findIndex((h, l, c) => {
          const d = c[l + 1];
          return h.originalLeft < t.originalLeft && (!d || t.originalLeft < d.originalLeft);
        });
        if (o !== -1) {
          const h = r.barrages[o], l = r.barrages[o + 1];
          if (h.originalRight + this.minSpace < t.originalLeft && (!l || t.originalRight + this.minSpace < l.originalLeft)) {
            t.show = !0, r.barrages.splice(o + 1, 0, t), t.top = r.top, i = !0;
            break;
          }
        }
      }
    }
    i || this.randomTrackBarrage(t);
  }
  /**
   * 发送新的弹幕
   * @param scrollBarrage 滚动弹幕实例
   */
  send(t) {
    t.grade = Math.ceil(t.height / this.realTrackHeight), this.br.renderConfig.avoidOverlap ? this.avoidOverlapInsert(t) : this.randomTrackBarrage(t), this.bLCalculate.canShowScrollBarrageInstances.push(t);
  }
  /**
   * 重置存放 实际轨道 和 虚拟轨道 的数组
   */
  resetTracks() {
    this.realTracks = [], this.virtualTracks = [], this.vtToVtsMap.clear(), this.gradeToVtsMap.clear();
  }
  /**
   * 处理高度变化重新进行布局计算
   * @param scrollBarrages 滚动弹幕实例数组
   */
  heightChangeReLayoutCalc(t) {
    this.resetTracks(), this.layoutScrollBarrages(t);
  }
  /**
   * 随机一个实际轨道并设置弹幕
   * @param barrage 滚动弹幕实例
   */
  randomTrackBarrage(t) {
    const i = this.getRandomRealTrack();
    t.top = i.top, t.show = !0;
  }
  /**
   * 是否允许弹幕相互重叠
   */
  get avoidOverlap() {
    return this.br.renderConfig.avoidOverlap;
  }
  /**
   * 是否打印关键数据
   */
  get isLogKeyData() {
    return this.br.devConfig.isLogKeyData;
  }
  /**
   * 获取一个随机的实际轨道
   */
  getRandomRealTrack() {
    return this.realTracks[g.Math.getRandomInt(0, this.realTracks.length - 1)];
  }
  /**
   * 获取滚动弹幕的最小间距
   */
  get minSpace() {
    return this.br.renderConfig.minSpace;
  }
}
class X {
  constructor(t, i) {
    // 实际轨道的唯一 id
    a(this, "id");
    // 实际轨道的高度
    a(this, "height");
    this.id = t, this.height = i;
  }
  /**
   * 当前实际轨道的 top
   */
  get top() {
    return (this.id - 1) * this.height;
  }
}
class Y {
  constructor(t, i, e) {
    // 虚拟轨道的唯一 id
    a(this, "id");
    // 当前虚拟轨道包含的实际轨道，数组形式
    a(this, "rtIdArr");
    a(this, "rtIdSet");
    a(this, "rtInstanceArr");
    // 当前虚拟轨道包含的滚动弹幕
    a(this, "barrages", []);
    this.id = t, this.rtIdArr = i, this.rtIdSet = new Set(i), this.rtInstanceArr = e;
  }
  /**
   * 获取轨道中的最后一个滚动弹幕
   */
  getLastBarrage() {
    return this.barrages[this.barrages.length - 1];
  }
  /**
   * 向虚拟轨道中添加新的滚动弹幕
   * @param barrage
   */
  push(t) {
    this.barrages.push(t);
  }
  /**
   * 清空所有的弹幕
   */
  clearBarrage() {
    this.barrages = [];
  }
  /**
   * 获取指定下标的滚动弹幕
   * @param index
   */
  getByIndex(t) {
    return this.barrages[t];
  }
  /**
   * 当前虚拟轨道的级别（包含虚拟轨道的数量）
   */
  get grade() {
    return this.rtIdArr.length;
  }
  /**
   * 当前弹幕是不是空的
   */
  get isEmpty() {
    return this.barrages.length === 0;
  }
  /**
   * 当前虚拟轨道的 top
   */
  get top() {
    return this.rtInstanceArr[0].top;
  }
}
class $ {
  constructor({
    barrageRenderer: t
  }) {
    a(this, "br");
    a(this, "allBarrageInstances", []);
    a(this, "fixedBarrageInstances", []);
    a(this, "scrollBarrageInstances", []);
    // 实际能够渲染出来的滚动弹幕
    a(this, "canShowScrollBarrageInstances", []);
    a(this, "seniorBarrageInstances", []);
    // 固定弹幕布局计算器
    a(this, "fixedBarrageLayout");
    a(this, "virtualTrackAlgorithm");
    this.br = t, this.fixedBarrageLayout = new W(this.br), this.virtualTrackAlgorithm = new V(this.br, this);
  }
  /**
   * 设置弹幕数据
   * @param barrageOptions 弹幕配置数组
   */
  setBarrages(t) {
    let i = t.map((e) => {
      switch (e.barrageType) {
        case "top":
        case "bottom":
          return new B(e, this.br);
        case "scroll":
          return new x(e, this.br);
        case "senior":
          return new y(e, this.br);
      }
    });
    i = i.sort((e, s) => e.time - s.time), this.allBarrageInstances = i, this.scrollBarrageInstances = i.filter((e) => e.barrageType === "scroll"), this.fixedBarrageInstances = i.filter((e) => ["top", "bottom"].includes(e.barrageType)), this.seniorBarrageInstances = i.filter((e) => e.barrageType === "senior"), this.virtualTrackAlgorithm.layoutScrollBarrages(this.scrollBarrageInstances);
  }
  /**
   * 获取某一时刻需要渲染的弹幕，交由外部进行渲染
   * @param time 视频播放时间点
   */
  getRenderBarrages(t) {
    const i = this.getRenderScrollBarrages(t), e = this.getRenderFixedBarrages(t), s = this.getRenderSeniorBarrages(t);
    return [
      ...i,
      ...e,
      ...s
    ].sort((r, o) => r.prior !== o.prior ? r.prior ? 1 : -1 : r.time - o.time);
  }
  /**
   * 发送新的弹幕
   * @param barrage 弹幕配置对象
   */
  send(t) {
    if (t.barrageType === "scroll") {
      const i = new x(t, this.br);
      g.Algorithm.insertBarrageByTime(this.scrollBarrageInstances, i), this.virtualTrackAlgorithm.send(i);
    } else if (t.barrageType === "top" || t.barrageType === "bottom") {
      const i = new B(t, this.br);
      g.Algorithm.insertBarrageByTime(this.fixedBarrageInstances, i), this.fixedBarrageLayout.send(i);
    } else if (t.barrageType === "senior") {
      const i = new y(t, this.br);
      g.Algorithm.insertBarrageByTime(this.seniorBarrageInstances, i);
    }
  }
  /**
   * 获取当前应该渲染的滚动弹幕
   * @param time 视频播放时间点，毫秒为单位
   */
  getRenderScrollBarrages(t) {
    return this.canShowScrollBarrageInstances.filter((i) => {
      const e = t >= i.time && t <= i.endTime;
      return e && (i.left = i.originalLeft - // 弹幕整体向左移动的总距离，时间 * 速度
      t / 1e3 * (i.barrageSpeed || this.br.renderConfig.speed)), e;
    });
  }
  /**
   * 获取当前应该渲染的固定弹幕
   * @param time 视频播放时间点
   */
  getRenderFixedBarrages(t) {
    return this.fixedBarrageLayout.getRenderFixedBarrages(this.fixedBarrageInstances, t);
  }
  /**
   * 获取当前应该渲染的高级弹幕
   * @param time 视频播放时间点
   */
  getRenderSeniorBarrages(t) {
    const i = this.seniorBarrageInstances.filter(
      (e) => (
        // 当前时间大于等于弹幕的出现时间 并且 当前时间小于等于弹幕的结束时间
        t >= e.time && t <= e.time + e.seniorBarrageConfig.totalDuration
      )
    );
    return i.forEach((e) => {
      const s = e.time, r = s + e.seniorBarrageConfig.delay, o = r + e.seniorBarrageConfig.motionDuration;
      if (t >= s && t <= r)
        e.left = e.actualStartLocation.x, e.top = e.actualStartLocation.y;
      else if (t >= r && t <= o) {
        const h = t - r;
        e.left = e.actualStartLocation.x + h * e.vx, e.top = e.actualStartLocation.y + h * e.vy;
      } else
        e.left = e.actualEndLocation.x, e.top = e.actualEndLocation.y;
    }), i;
  }
  /**
   * 处理宽度 change
   */
  handleWidthChange() {
    this.fixedBarrageInstances.forEach((t) => t.calcFixedBarrageLeft()), this.scrollBarrageInstances.forEach((t) => t.calcOriginal());
  }
  /**
   * 处理高度 change
   */
  handleHeightChange() {
    this.fixedBarrageLayout.clearStoredBarrage(), this.virtualTrackAlgorithm.heightChangeReLayoutCalc(this.scrollBarrageInstances);
  }
  /**
   * 尺寸发生变化的时候调用，会重新计算内部数据
   * @param type 尺寸变化的类型
   */
  resize(t) {
    this.seniorBarrageInstances.forEach((i) => i.calcActualLocation()), t === "ONLY_WIDTH" ? this.handleWidthChange() : t === "ONLY_HEIGHT" ? this.handleHeightChange() : (this.handleWidthChange(), this.handleHeightChange());
  }
  /**
   * 根据 render Config change 进行布局方面的重新计算
   * @param isSpeedChange        重新计算 originalLeft，如果当前是不允许遮挡的话，重新进行虚拟轨道计算；
   * @param isHeightReduceChange 重置轨道数据，根据 avoidOverlap 进行重新布局，清空固定弹幕的 store
   * @param isRenderRegionChange 重置轨道数据，根据 avoidOverlap 进行重新布局
   * @param isAvoidOverlapChange  					根据 avoidOverlap 进行重新布局
   * @param isMinSpaceChange     如果当前是不允许遮挡的话，重新进行虚拟轨道计算；
   */
  renderConfigChange(t, i, e, s, r) {
    t && this.scrollBarrageInstances.forEach((o) => o.calcOriginal()), i && this.fixedBarrageLayout.clearStoredBarrage(), (i || e) && this.virtualTrackAlgorithm.resetTracks(), (t && this.br.renderConfig.avoidOverlap || i || e || s || r && this.br.renderConfig.avoidOverlap) && this.virtualTrackAlgorithm.layoutScrollBarrages(this.scrollBarrageInstances);
  }
}
var p = /* @__PURE__ */ ((n) => (n[n.FIXED_DURATION_ERROR = 1] = "FIXED_DURATION_ERROR", n[n.SENIOR_TOTAL_ERROR = 2] = "SENIOR_TOTAL_ERROR", n[n.SENIOR_DELAY_ERROR = 3] = "SENIOR_DELAY_ERROR", n[n.SENIOR_MOTION_ERROR = 4] = "SENIOR_MOTION_ERROR", n))(p || {});
class m extends Error {
  constructor(i) {
    super(i.message);
    a(this, "code");
    this.code = i.code;
  }
}
class U {
  constructor(t) {
    a(this, "br");
    a(this, "imageBitmapCache", /* @__PURE__ */ new Map());
    this.br = t;
  }
  /**
   * 获取渲染弹幕对应的 ImageBitmap。
   * 因为 createImageBitmap 是一个异步操作，所以当缓存中有的话，返回 ImageBitmap，
   * 缓存中没有的话，直接返回 undefined。
   * @param barrage 需要渲染的弹幕
   */
  getImageBitmap(t) {
    if (this.imageBitmapCache.has(t.id))
      return this.imageBitmapCache.get(t.id);
    const { width: i, height: e } = this.preRenderCanvas;
    this.preRenderCanvasCtx.clearRect(0, 0, i, e), t.setCtxFont(this.preRenderCanvasCtx), t.sections.forEach((s) => {
      s.sectionType === "text" ? (g.Color.isVisibleColor(this.br.renderConfig.strokeStyle) && this.br.renderConfig.lineWidth > 0 && this.preRenderCanvasCtx.strokeText(s.text, s.leftOffset, s.topOffset), this.preRenderCanvasCtx.fillText(s.text, s.leftOffset, s.topOffset)) : s.sectionType === "image" && this.preRenderCanvasCtx.drawImage(
        g.Cache.imageElementFactory(s.url),
        s.leftOffset,
        s.topOffset,
        s.width,
        s.height
      );
    }), createImageBitmap(
      this.preRenderCanvas,
      0,
      0,
      t.width * this.dpr,
      t.height * this.dpr
    ).then((s) => {
      this.imageBitmapCache.set(t.id, s);
    });
  }
  /**
   * 为了避免 ImageBitmap 占用太多的内存，需要在切换渲染弹幕集的时候，进行 ImageBitmap 的清空
   */
  clear() {
    this.imageBitmapCache.clear();
  }
  get preRenderCanvas() {
    return this.br.preRenderCanvas;
  }
  get preRenderCanvasCtx() {
    return this.br.preRenderCanvasCtx;
  }
  get dpr() {
    return this.br.dpr;
  }
}
class J {
  constructor({
    container: t,
    video: i,
    barrages: e,
    barrageImages: s,
    renderConfig: r,
    devConfig: o,
    beforeFrameRender: h,
    afterFrameRender: l,
    beforeBarrageRender: c,
    afterBarrageRender: d,
    mask: f
  }) {
    // 容器 DOM
    a(this, "container");
    // video 元素
    a(this, "video");
    // Canvas 元素
    a(this, "canvas");
    // Canvas 渲染上下文;
    a(this, "ctx");
    // 弹幕中渲染图片的配置
    a(this, "barrageImages");
    // 默认渲染配置
    a(this, "defaultRenderConfig", {
      heightReduce: 0,
      speed: 200,
      opacity: 1,
      renderRegion: 1,
      fontFamily: "Microsoft YaHei",
      fontWeight: "normal",
      avoidOverlap: !0,
      minSpace: 10,
      ...G,
      ...K
    });
    // 渲染配置
    a(this, "renderConfig", this.defaultRenderConfig);
    // 默认开发配置
    a(this, "defaultDevConfig", {
      isRenderFPS: !1,
      isRenderBarrageBorder: !1,
      isLogKeyData: !1
    });
    // 开发相关配置
    a(this, "devConfig", this.defaultDevConfig);
    // 弹幕布局计算器
    a(this, "barrageLayoutCalculate", new $({
      barrageRenderer: this
    }));
    // 用于标识弹幕功能是否被打开
    a(this, "isOpen", !0);
    a(this, "animationHandle");
    a(this, "fps", "");
    a(this, "lastFrameTime");
    a(this, "lastCalcTime", 0);
    // 记录上次布局计算时，container 的宽高
    a(this, "lastContainerSize", { width: 0, height: 0 });
    // 离屏 canvas 优化
    a(this, "offscreenCanvas");
    a(this, "offscreenCanvasCtx");
    // 用于生成 ImageBitmap 预渲染数据的 canvas
    a(this, "preRenderCanvas");
    a(this, "preRenderCanvasCtx");
    // 预渲染优化器
    a(this, "preRenderOptimizer");
    // 显示物理设备 dpr
    a(this, "dpr", g.Canvas.getDevicePixelRatio());
    // 一系列钩子函数
    // 每一帧渲染前的钩子函数
    a(this, "beforeFrameRender");
    // 每一帧渲染后的钩子函数
    a(this, "afterFrameRender");
    // 每个弹幕渲染前的钩子函数
    a(this, "beforeBarrageRender");
    // 每个弹幕渲染后的钩子函数
    a(this, "afterBarrageRender");
    // barrage click callback
    a(this, "onBarrageClick");
    a(this, "clickHandler", (e) => this.handleClick(e));
    // 蒙版数据，如果蒙版数据存在的话，每一帧的渲染中都会用到，用于实现人像免遮挡
    a(this, "mask", {
      type: null,
      data: null
    });
    this.video = i, this.container = typeof t == "string" ? document.getElementById(t) : t, this.canvas = document.createElement("canvas"), this.ctx = this.canvas.getContext("2d"), this.offscreenCanvas = document.createElement("canvas"), this.offscreenCanvasCtx = this.offscreenCanvas.getContext("2d"), this.preRenderCanvas = document.createElement("canvas"), this.preRenderCanvasCtx = this.preRenderCanvas.getContext("2d"), this.handleDOM(), this.container && this.container.addEventListener("click", this.clickHandler, !0), this.preRenderOptimizer = new U(this), this.setRenderConfigInternal(r || {}, !0), this.setDevConfig(o || {}), this.barrageImages = s, this.setBarrages(e), this._setCtxFontStrokeShadow(), this.beforeFrameRender = h, this.afterFrameRender = l, this.beforeBarrageRender = c, this.afterBarrageRender = d, this.setMask(f), this.devConfig.isLogKeyData && console.log("全局实例：", this);
  }
  /**
   * 处理 DOM 相关
   */
  handleDOM() {
    this.container || console.error("Unable to obtain container element"), this.ctx || console.error("Unable to obtain CanvasRenderingContext2D"), !(!this.container || !this.ctx) && (this.container.style.position = "relative", this.canvas.style.position = "absolute", this.canvas.style.left = "0px", this.canvas.style.top = "0px", this.canvas.style.pointerEvents = "none", this.canvas.width = this.container.clientWidth, this.canvas.height = this.container.clientHeight - (this.renderConfig.heightReduce ?? 0), this.container.appendChild(this.canvas), this.handleHighDprVague(this.canvas, this.ctx), this.offscreenCanvas.width = this.container.clientWidth, this.offscreenCanvas.height = this.container.clientHeight - (this.renderConfig.heightReduce ?? 0), this.handleHighDprVague(this.offscreenCanvas, this.offscreenCanvasCtx), this.preRenderCanvas.width = this.container.clientWidth, this.preRenderCanvas.height = this.container.clientHeight - (this.renderConfig.heightReduce ?? 0), this.handleHighDprVague(this.preRenderCanvas, this.preRenderCanvasCtx));
  }
  /**
   * 处理 Canvas 在高分屏上渲染模糊的问题
   */
  /**
   * Handle barrage click (hit test + callback)
   */
  handleClick(e) {
    if (!this.onBarrageClick || !this.canvas) return;
    const rect = this.canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    let barrages = this.barrageLayoutCalculate.getRenderBarrages(this.progress);
    if (this.renderConfig.barrageFilter) {
      barrages = barrages.filter((b) => this.renderConfig.barrageFilter(b));
    }
    for (let i = barrages.length - 1; i >= 0; i--) {
      const b = barrages[i];
      if (b.top !== void 0 && b.left !== void 0 && x >= b.left && x <= b.left + b.width && y >= b.top && y <= b.top + b.height) {
        e.preventDefault();
        e.stopPropagation();
        this.onBarrageClick(b);
        return;
      }
    }
  }

  handleHighDprVague(t, i) {
    const e = t.width, s = t.height;
    t.width = e * this.dpr, t.height = s * this.dpr, t.style.width = e + "px", t.style.height = s + "px", i.scale(this.dpr, this.dpr), i.textBaseline = "hanging";
  }
  /**
   * 发送新的弹幕
   * @param barrage 弹幕配置对象
   */
  send(t) {
    const i = this.validateBarrageOption(t);
    if (i !== !0)
      throw i;
    this.barrageLayoutCalculate.send(t);
  }
  /**
   * container 元素尺寸变更后，调用进行重新计算
   */
  resize() {
    var o, h;
    this.handleDOM();
    const t = {
      width: ((o = this.container) == null ? void 0 : o.clientWidth) || 0,
      height: ((h = this.container) == null ? void 0 : h.clientHeight) || 0
    }, { width: i, height: e } = this.lastContainerSize, s = i !== t.width, r = e !== t.height;
    (s || r) && (this.lastContainerSize = t, s && !r ? this.barrageLayoutCalculate.resize("ONLY_WIDTH") : !s && r ? this.barrageLayoutCalculate.resize("ONLY_HEIGHT") : this.barrageLayoutCalculate.resize("BOTH")), this._setCtxFontStrokeShadow(), this.renderFrame();
  }
  /**
   * 设置弹幕数据
   * @param barrages 弹幕配置对象数组
   */
  setBarrages(t) {
    var i, e;
    t && (this.preRenderOptimizer.clear(), t = t.filter((s) => this.validateBarrageOption(s) === !0), this.barrageLayoutCalculate.setBarrages(t), this.lastContainerSize = {
      width: ((i = this.container) == null ? void 0 : i.clientWidth) || 0,
      height: ((e = this.container) == null ? void 0 : e.clientHeight) || 0
    });
  }
  /**
   * 设置渲染配置（可以部分设置配置）
   * @param renderConfig 配置对象
   * @param init 是不是初始化
   */
  setRenderConfigInternal(t, i = !1) {
    this.preRenderOptimizer.clear();
    const e = Object.keys(t), s = e.includes("speed") && t.speed !== this.renderConfig.speed, r = e.includes("heightReduce") && t.heightReduce !== this.renderConfig.heightReduce, o = e.includes("renderRegion") && t.renderRegion !== this.renderConfig.renderRegion, h = e.includes("avoidOverlap") && t.avoidOverlap !== this.renderConfig.avoidOverlap, l = e.includes("minSpace") && t.minSpace !== this.renderConfig.minSpace;
    Object.assign(this.renderConfig, t), this._setCtxFontStrokeShadow(), !i && (s || r || o || h) && (r && this.handleDOM(), this.barrageLayoutCalculate.renderConfigChange(
      s,
      r,
      o,
      h,
      l
    )), !this.animationHandle && !i && this._render();
  }
  /**
   * 设置渲染配置（可以部分设置配置）
   * @param renderConfig 渲染配置
   */
  setRenderConfig(t) {
    this.setRenderConfigInternal(t);
  }
  /**
   * 设置开发配置（可以部分设置配置）
   * @param devConfig 开发配置
   */
  setDevConfig(t) {
    Object.assign(this.devConfig, t);
  }
  /**
   * 负责每一帧的渲染
   * @private
   */
  _render() {
    if (!this.isOpen)
      return;
    let t = this.barrageLayoutCalculate.getRenderBarrages(this.progress);
    if (this.renderConfig.barrageFilter && (t = t.filter((i) => this.renderConfig.barrageFilter(i))), this.offscreenCanvasCtx.clearRect(0, 0, this.offscreenCanvas.width, this.offscreenCanvas.height), this.beforeFrameRender && this.beforeFrameRender({
      ctx: this.offscreenCanvasCtx,
      br: this
    }), this.offscreenCanvasCtx.save(), this.offscreenCanvasCtx.globalAlpha = this.renderConfig.opacity, t.forEach((i) => {
      i.render(this.offscreenCanvasCtx);
    }), this.devConfig.isRenderFPS && this.renderFps(), this.offscreenCanvasCtx.restore(), this.afterFrameRender && this.afterFrameRender({
      ctx: this.offscreenCanvasCtx,
      br: this
    }), this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height), this.ctx.save(), this.mask.data) {
      const { type: i, data: e } = this.mask;
      i === "URL" ? this.ctx.drawImage(
        e,
        0,
        0,
        this.canvas.width,
        this.canvas.height
      ) : i === "ImageData" && this.ctx.putImageData(
        e,
        0,
        0
      ), this.ctx.globalCompositeOperation = "source-out";
    }
    this.ctx.drawImage(
      this.offscreenCanvas,
      0,
      0,
      this.offscreenCanvas.width,
      this.offscreenCanvas.height,
      0,
      0,
      this.canvas.width / this.dpr,
      this.canvas.height / this.dpr
    ), this.ctx.restore(), this.animationHandle && requestAnimationFrame(() => this._render());
  }
  /**
   * 创建动画任务
   * @private
   */
  _createAnimation() {
    !this.animationHandle && this.isOpen && (this.animationHandle = requestAnimationFrame(() => this._render()));
  }
  /**
   * 设置字体描边以及阴影
   * @private
   */
  _setCtxFontStrokeShadow() {
    const {
      strokeStyle: t,
      lineWidth: i,
      lineCap: e,
      lineJoin: s,
      miterLimit: r,
      shadowColor: o,
      shadowBlur: h,
      shadowOffsetX: l,
      shadowOffsetY: c
    } = this.renderConfig;
    this.offscreenCanvasCtx.strokeStyle = t, this.offscreenCanvasCtx.lineWidth = i, this.offscreenCanvasCtx.lineCap = e, this.offscreenCanvasCtx.lineJoin = s, this.offscreenCanvasCtx.miterLimit = r, this.offscreenCanvasCtx.shadowColor = o, this.offscreenCanvasCtx.shadowBlur = h, this.offscreenCanvasCtx.shadowOffsetX = l, this.offscreenCanvasCtx.shadowOffsetY = c, this.preRenderCanvasCtx.strokeStyle = t, this.preRenderCanvasCtx.lineWidth = i, this.preRenderCanvasCtx.lineCap = e, this.preRenderCanvasCtx.lineJoin = s, this.preRenderCanvasCtx.miterLimit = r, this.preRenderCanvasCtx.shadowColor = o, this.preRenderCanvasCtx.shadowBlur = h, this.preRenderCanvasCtx.shadowOffsetX = l, this.preRenderCanvasCtx.shadowOffsetY = c;
  }
  /**
   * 当前动画的播放进度，单位：毫秒
   */
  get progress() {
    return this.videoStatus.currentTime;
  }
  /**
   * video 的状态
   */
  get videoStatus() {
    return {
      // 当前视频的播放进度（ms）
      currentTime: this.video.currentTime * 1e3,
      // 当前视频是不是播放中
      playing: !this.video.paused
    };
  }
  /**
   * canvas 的尺寸
   */
  get canvasSize() {
    return {
      width: this.canvas.width / this.dpr,
      height: this.canvas.height / this.dpr
    };
  }
  /**
   * 弹幕运行速度，仅对滚动弹幕有效（每毫秒多少像素）
   */
  get speedPerMs() {
    return this.renderConfig.speed / 1e3;
  }
  /**
   * 触发一帧的渲染
   */
  renderFrame() {
    this.animationHandle || this._render();
  }
  /**
   * 执行弹幕的播放
   */
  play() {
    this._createAnimation();
  }
  /**
   * 暂停弹幕的播放
   */
  pause() {
    this.animationHandle && cancelAnimationFrame(this.animationHandle), this.animationHandle = void 0;
  }
  /**
   * 是否打开弹幕
   * @param isOpen 是否打开弹幕
   */
  switch(t) {
    this.isOpen = t, t ? this.videoStatus.playing ? this._createAnimation() : this._render() : (this.animationHandle && cancelAnimationFrame(this.animationHandle), this.animationHandle = void 0, this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height));
  }
  /**
   * 设置蒙版数据
   * @param mask 图片的 url 或者 ImageData 数据
   */
  setMask(t) {
    typeof t == "string" ? (this.mask.type = "URL", g.Cache.loadImage(t).then((i) => {
      this.mask.data = i;
    })) : t instanceof ImageData ? (this.mask.type = "ImageData", this.mask.data = t) : (this.mask.type = null, this.mask.data = null);
  }
  /**
   * 渲染 FPS
   */
  renderFps() {
    const t = Date.now();
    this.lastFrameTime && t - this.lastCalcTime > 200 && (this.fps = `${Math.floor(1e3 / (t - this.lastFrameTime))}FPS`, this.lastCalcTime = t), this.lastFrameTime = t, this.fps && (this.offscreenCanvasCtx.font = "bold 32px Microsoft YaHei", this.offscreenCanvasCtx.fillStyle = "blue", this.offscreenCanvasCtx.fillText(this.fps, 20, 30));
  }
  /**
   * 判断弹幕数据是否合规
   * @param barrage 弹幕配置对象
   */
  validateBarrageOption(t) {
    if ((t.barrageType === "top" || t.barrageType === "bottom") && t.duration <= 0)
      return new m({
        code: p.FIXED_DURATION_ERROR,
        message: "The duration of the fixed barrage should be greater than 0"
      });
    if (t.barrageType === "senior") {
      const {
        totalDuration: i,
        delay: e,
        motionDuration: s
      } = t.seniorBarrageConfig;
      if (i <= 0)
        return new m({
          code: p.SENIOR_TOTAL_ERROR,
          message: "The totalDuration of senior barrage should be greater than 0"
        });
      if (e < 0)
        return new m({
          code: p.SENIOR_DELAY_ERROR,
          message: "The delay of senior barrage should be greater than or equal to 0"
        });
      if (s < 0)
        return new m({
          code: p.SENIOR_MOTION_ERROR,
          message: "The motionDuration of senior barrage should be greater than or equal to 0"
        });
    }
    return !0;
  }
}
const G = {
  strokeStyle: "rgba(0, 0, 0, 0)",
  lineWidth: 1,
  lineCap: "butt",
  lineJoin: "miter",
  miterLimit: 10
}, K = {
  shadowColor: "rgba(0, 0, 0, 0)",
  shadowBlur: 0,
  shadowOffsetX: 0,
  shadowOffsetY: 0
};
export {
  m as BarrageOptionError,
  J as BarrageRenderer,
  v as BaseBarrage,
  K as DEFAULT_FONT_SHADOW,
  G as DEFAULT_FONT_STROKE,
  p as ErrorCode,
  B as FixedBarrage,
  N as ImageSection,
  x as ScrollBarrage,
  y as SeniorBarrage,
  P as TextSection,
  J as default
};
