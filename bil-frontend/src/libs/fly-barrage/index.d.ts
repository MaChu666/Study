/**
 * 弹幕中渲染图片的配置
 */
export declare type BarrageImage = {
    id: string;
    url: string;
    width: number;
    height: number;
};

/**
 * 弹幕计算（用于滚动、顶部、底部、高级弹幕的计算）
 * 输入：弹幕数据、当前时间；
 * 输出：当前时间应该渲染的弹幕；
 */
declare class BarrageLayoutCalculate {
    br: BarrageRenderer;
    allBarrageInstances: BaseBarrage[];
    fixedBarrageInstances: FixedBarrage[];
    scrollBarrageInstances: ScrollBarrage[];
    canShowScrollBarrageInstances: ScrollBarrage[];
    seniorBarrageInstances: SeniorBarrage[];
    fixedBarrageLayout: FixedBarrageLayout;
    virtualTrackAlgorithm: VirtualTrackAlgorithm;
    constructor({ barrageRenderer, }: BarrageLayoutCalculateOptions);
    /**
     * 设置弹幕数据
     * @param barrageOptions 弹幕配置数组
     */
    setBarrages(barrageOptions: BarrageOptions[]): void;
    /**
     * 获取某一时刻需要渲染的弹幕，交由外部进行渲染
     * @param time 视频播放时间点
     */
    getRenderBarrages(time: number): BaseBarrage[];
    /**
     * 发送新的弹幕
     * @param barrage 弹幕配置对象
     */
    send(barrage: BarrageOptions): void;
    /**
     * 获取当前应该渲染的滚动弹幕
     * @param time 视频播放时间点，毫秒为单位
     */
    getRenderScrollBarrages(time: number): ScrollBarrage[];
    /**
     * 获取当前应该渲染的固定弹幕
     * @param time 视频播放时间点
     */
    getRenderFixedBarrages(time: number): FixedBarrage[];
    /**
     * 获取当前应该渲染的高级弹幕
     * @param time 视频播放时间点
     */
    getRenderSeniorBarrages(time: number): SeniorBarrage[];
    /**
     * 处理宽度 change
     */
    private handleWidthChange;
    /**
     * 处理高度 change
     */
    private handleHeightChange;
    /**
     * 尺寸发生变化的时候调用，会重新计算内部数据
     * @param type 尺寸变化的类型
     */
    resize(type: 'ONLY_WIDTH' | 'ONLY_HEIGHT' | 'BOTH'): void;
    /**
     * 根据 render Config change 进行布局方面的重新计算
     * @param isSpeedChange        重新计算 originalLeft，如果当前是不允许遮挡的话，重新进行虚拟轨道计算；
     * @param isHeightReduceChange 重置轨道数据，根据 avoidOverlap 进行重新布局，清空固定弹幕的 store
     * @param isRenderRegionChange 重置轨道数据，根据 avoidOverlap 进行重新布局
     * @param isAvoidOverlapChange  					根据 avoidOverlap 进行重新布局
     * @param isMinSpaceChange     如果当前是不允许遮挡的话，重新进行虚拟轨道计算；
     */
    renderConfigChange(isSpeedChange: boolean, isHeightReduceChange: boolean, isRenderRegionChange: boolean, isAvoidOverlapChange: boolean, isMinSpaceChange: boolean): void;
}

declare type BarrageLayoutCalculateOptions = {
    barrageRenderer: BarrageRenderer;
};

/**
 * 弹幕配置对象的类型错误
 */
export declare class BarrageOptionError extends Error {
    code: ErrorCode;
    constructor(params: {
        message: string;
        code: ErrorCode;
    });
}

/**
 * 面向用户的弹幕配置
 */
export declare type BarrageOptions = ScrollBarrageOptions | FixedBarrageOptions | SeniorBarrageOptions;

/**
 * 弹幕渲染器
 */
declare class BarrageRenderer {
    container: HTMLElement | null;
    video: HTMLVideoElement;
    canvas: HTMLCanvasElement;
    ctx: CanvasRenderingContext2D;
    barrageImages?: BarrageImage[];
    defaultRenderConfig: RenderConfig;
    renderConfig: RenderConfig;
    defaultDevConfig: DevConfig;
    devConfig: DevConfig;
    barrageLayoutCalculate: BarrageLayoutCalculate;
    isOpen: boolean;
    animationHandle?: number;
    fps: string;
    lastFrameTime?: number;
    lastCalcTime: number;
    lastContainerSize: {
        width: number;
        height: number;
    };
    offscreenCanvas: HTMLCanvasElement;
    offscreenCanvasCtx: CanvasRenderingContext2D;
    preRenderCanvas: HTMLCanvasElement;
    preRenderCanvasCtx: CanvasRenderingContext2D;
    preRenderOptimizer: PreRenderOptimizer;
    dpr: number;
    beforeFrameRender?: FrameRenderHook;
    afterFrameRender?: FrameRenderHook;
    beforeBarrageRender?: BarrageRenderHook;
    afterBarrageRender?: BarrageRenderHook;
    mask: Mask;
    constructor({ container, video, barrages, barrageImages, renderConfig, devConfig, beforeFrameRender, afterFrameRender, beforeBarrageRender, afterBarrageRender, mask, }: RendererOptions);
    /**
     * 处理 DOM 相关
     */
    private handleDOM;
    /**
     * 处理 Canvas 在高分屏上渲染模糊的问题
     */
    handleHighDprVague(canvas: HTMLCanvasElement, ctx: CanvasRenderingContext2D): void;
    /**
     * 发送新的弹幕
     * @param barrage 弹幕配置对象
     */
    send(barrage: BarrageOptions): void;
    /**
     * container 元素尺寸变更后，调用进行重新计算
     */
    resize(): void;
    /**
     * 设置弹幕数据
     * @param barrages 弹幕配置对象数组
     */
    setBarrages(barrages?: BarrageOptions[]): void;
    /**
     * 设置渲染配置（可以部分设置配置）
     * @param renderConfig 配置对象
     * @param init 是不是初始化
     */
    private setRenderConfigInternal;
    /**
     * 设置渲染配置（可以部分设置配置）
     * @param renderConfig 渲染配置
     */
    setRenderConfig(renderConfig: Partial<RenderConfig>): void;
    /**
     * 设置开发配置（可以部分设置配置）
     * @param devConfig 开发配置
     */
    setDevConfig(devConfig: Partial<DevConfig>): void;
    /**
     * 负责每一帧的渲染
     * @private
     */
    private _render;
    /**
     * 创建动画任务
     * @private
     */
    private _createAnimation;
    /**
     * 设置字体描边以及阴影
     * @private
     */
    private _setCtxFontStrokeShadow;
    /**
     * 当前动画的播放进度，单位：毫秒
     */
    get progress(): number;
    /**
     * video 的状态
     */
    get videoStatus(): {
        currentTime: number;
        playing: boolean;
    };
    /**
     * canvas 的尺寸
     */
    get canvasSize(): {
        width: number;
        height: number;
    };
    /**
     * 弹幕运行速度，仅对滚动弹幕有效（每毫秒多少像素）
     */
    get speedPerMs(): number;
    /**
     * 触发一帧的渲染
     */
    renderFrame(): void;
    /**
     * 执行弹幕的播放
     */
    play(): void;
    /**
     * 暂停弹幕的播放
     */
    pause(): void;
    /**
     * 是否打开弹幕
     * @param isOpen 是否打开弹幕
     */
    switch(isOpen: boolean): void;
    /**
     * 设置蒙版数据
     * @param mask 图片的 url 或者 ImageData 数据
     */
    setMask(mask?: string | ImageData): void;
    /**
     * 渲染 FPS
     */
    private renderFps;
    /**
     * 判断弹幕数据是否合规
     * @param barrage 弹幕配置对象
     */
    private validateBarrageOption;
}
export { BarrageRenderer }
export default BarrageRenderer;

/**
 * 弹幕渲染钩子函数的类型
 */
export declare type BarrageRenderHook = (data: {
    ctx: CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D;
    br: BarrageRenderer;
    barrage: BaseBarrage;
}) => void;

/**
 * 弹幕的类型
 * scroll：滚动弹幕
 * top：顶部弹幕
 * bottom：底部弹幕
 * senior：高级弹幕
 */
export declare type BarrageType = 'scroll' | 'top' | 'bottom' | 'senior';

/**
 * 弹幕类
 */
export declare abstract class BaseBarrage {
    id: string;
    readonly abstract barrageType: BarrageType;
    time: number;
    text: string;
    fontSize: number;
    lineHeight: number;
    color: string;
    prior: boolean;
    customRender?: CustomRender;
    addition?: Record<string, any>;
    br: BarrageRenderer;
    top: number;
    left: number;
    width: number;
    height: number;
    sections: Section[];
    protected constructor({ id, time, text, fontSize, lineHeight, color, prior, customRender, addition, }: BaseBarrageOptions, barrageRenderer: BarrageRenderer);
    /**
     * 进行当前弹幕相关数据的计算
     */
    initBarrage(): void;
    /**
     * 解析 text 内容
     * 文本内容[图片id]文本内容[图片id] => ['文本内容', '[图片id]', '文本内容', '[图片id]']
     * @param barrageText 弹幕文本
     */
    analyseText(barrageText: string): Segment[];
    /**
     * 将当前弹幕渲染到指定的上下文
     * @param ctx 渲染上下文
     */
    render(ctx: CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D): void;
    /**
     * 设置文字相关的属性
     * @param ctx 渲染上下文
     */
    setCtxFont(ctx: CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D): void;
}

/**
 * 构造函数参数对象
 */
export declare type BaseBarrageOptions = {
    id: string;
    time: number;
    text: string;
    fontSize: number;
    lineHeight: number;
    color: string;
    prior?: boolean;
    customRender?: CustomRender;
    addition?: {
        [key: string]: any;
    };
};

/**
 * 自定义 render 相关配置
 */
export declare type CustomRender = {
    width: number;
    height: number;
    renderFn: RenderFn;
};

/**
 * 自定义 render 函数的参数
 */
export declare type CustomRenderOptions = {
    ctx: CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D;
    barrage: BaseBarrage;
    br: BarrageRenderer;
    imageElementFactory: (url: string) => HTMLImageElement;
};

export declare const DEFAULT_FONT_SHADOW: FontShadow;

export declare const DEFAULT_FONT_STROKE: FontStroke;

/**
 * 开发相关配置
 */
export declare type DevConfig = {
    isRenderFPS: boolean;
    isRenderBarrageBorder: boolean;
    isLogKeyData: boolean;
};

/**
 * 定义错误类型对应的 code
 */
export declare enum ErrorCode {
    /** 固定弹幕的持续时间应该大于 0 */
    FIXED_DURATION_ERROR = 1,
    /** 高级弹幕的生存时间应该大于 0 */
    SENIOR_TOTAL_ERROR = 2,
    /** 高级弹幕的延迟时间应该大于等于 0 */
    SENIOR_DELAY_ERROR = 3,
    /** 高级弹幕的运动时长应该大于等于 0 */
    SENIOR_MOTION_ERROR = 4
}

/**
 * 用于描述顶部弹幕、底部弹幕
 */
export declare class FixedBarrage extends BaseBarrage {
    readonly barrageType: BarrageType;
    duration: number;
    endTime: number;
    constructor(fixedBarrageOptions: FixedBarrageOptions, barrageRenderer: BarrageRenderer);
    /**
     * 计算固定弹幕的 left 属性
     */
    calcFixedBarrageLeft(): void;
}

/**
 * 用于处理顶部弹幕和底部弹幕的布局问题
 */
declare class FixedBarrageLayout {
    br: BarrageRenderer;
    topRenderBarrages: FixedBarrage[];
    bottomRenderBarrages: FixedBarrage[];
    constructor(br: BarrageRenderer);
    /**
     * 获取当前应该渲染的固定弹幕
     * @param allFixedBarrages 所有的固定弹幕数组
     * @param time 视频播放的时间点
     */
    getRenderFixedBarrages(allFixedBarrages: FixedBarrage[], time: number): FixedBarrage[];
    /**
     * 发送新的弹幕
     * @param barrage 弹幕实例
     */
    send(barrage: FixedBarrage): void;
    /**
     * 封装通用的工具方法
     * @param barrage 弹幕实例
     */
    insertFixedBarrage(barrage: FixedBarrage): void;
    /**
     * 清空缓存数组
     */
    clearStoredBarrage(): void;
    /**
     * 一半的 Canvas 高度，top 弹幕只能在 halfCanvasHeight 的上面，bottom 弹幕只能在 halfCanvasHeight 的下面
     */
    get middleHeightPoint(): number;
    /**
     * 顶部弹幕 y 轴方向的范围
     */
    get topRange(): number[];
    /**
     * topRange 的长度
     */
    get topRangeLength(): number;
    /**
     * 底部弹幕 y 轴方向的范围
     */
    get bottomRange(): number[];
    /**
     * bottomRange 的长度
     */
    get bottomRangeLength(): number;
}

export declare type FixedBarrageOptions = BaseBarrageOptions & {
    barrageType: 'top' | 'bottom';
    duration: number;
};

export declare type FontShadow = {
    shadowColor: string;
    shadowBlur: number;
    shadowOffsetX: number;
    shadowOffsetY: number;
};

export declare type FontStroke = {
    strokeStyle: string;
    lineWidth: number;
    lineCap: CanvasLineCap;
    lineJoin: CanvasLineJoin;
    miterLimit: number;
};

export declare type FontStrokeAndShadow = FontStroke & FontShadow;

/**
 * 帧渲染钩子函数的类型
 */
export declare type FrameRenderHook = (data: {
    ctx: CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D;
    br: BarrageRenderer;
}) => void;

export declare class ImageSection {
    readonly sectionType = "image";
    id: string;
    url: string;
    width: number;
    height: number;
    topOffset: number;
    leftOffset: number;
    constructor({ id, url, width, height, leftOffset, }: ImageSectionOptions);
}

/**
 * 描述弹幕内容中的图片片段
 */
export declare type ImageSectionOptions = BarrageImage & {
    topOffset?: number;
    leftOffset: number;
};

/**
 * 用于描述二位平面中的一点（单位 px）
 */
declare type Location_2 = {
    x: number;
    y: number;
};
export { Location_2 as Location }

/**
 * 用于描述二位平面中的一点（面向用户）
 */
export declare type LocationDefine = Location_2 & {
    type?: 'PIXEL' | 'PERCENT';
    offsetX?: number;
    offsetY?: number;
};

/**
 * 蒙版数据类型
 */
declare type Mask = {
    type: 'URL';
    data: HTMLImageElement;
} | {
    type: 'ImageData';
    data: ImageData;
} | {
    type: null;
    data: null;
};

/**
 * 预渲染优化器
 * 用于生成并缓存弹幕的 ImageBitmap，弹幕只需要在 Canvas 中渲染一次，以此保证性能
 */
declare class PreRenderOptimizer {
    br: BarrageRenderer;
    imageBitmapCache: Map<string, ImageBitmap>;
    constructor(br: BarrageRenderer);
    /**
     * 获取渲染弹幕对应的 ImageBitmap。
     * 因为 createImageBitmap 是一个异步操作，所以当缓存中有的话，返回 ImageBitmap，
     * 缓存中没有的话，直接返回 undefined。
     * @param barrage 需要渲染的弹幕
     */
    getImageBitmap(barrage: BaseBarrage): ImageBitmap | undefined;
    /**
     * 为了避免 ImageBitmap 占用太多的内存，需要在切换渲染弹幕集的时候，进行 ImageBitmap 的清空
     */
    clear(): void;
    get preRenderCanvas(): HTMLCanvasElement;
    get preRenderCanvasCtx(): CanvasRenderingContext2D;
    get dpr(): number;
}

/**
 * 用于描述实际轨道
 */
declare class RealTrack {
    id: number;
    height: number;
    constructor(id: number, height: number);
    /**
     * 当前实际轨道的 top
     */
    get top(): number;
}

/**
 * 弹幕渲染器渲染弹幕的配置
 */
export declare type RenderConfig = {
    barrageFilter?: (barrage: BaseBarrage) => boolean;
    priorBorderCustomRender?: RenderFn;
    heightReduce: number;
    speed: number;
    renderRegion: number;
    minSpace: number;
    avoidOverlap: boolean;
    opacity: number;
    fontFamily: string;
    fontWeight: string;
} & FontStrokeAndShadow;

/**
 * 弹幕渲染器 class 构造函数的参数
 */
export declare type RendererOptions = {
    container: string | HTMLElement;
    video: HTMLVideoElement;
    barrages?: BarrageOptions[];
    barrageImages?: BarrageImage[];
    renderConfig?: Partial<RenderConfig>;
    devConfig?: Partial<DevConfig>;
    beforeFrameRender?: FrameRenderHook;
    afterFrameRender?: FrameRenderHook;
    beforeBarrageRender?: BarrageRenderHook;
    afterBarrageRender?: BarrageRenderHook;
    mask?: string | ImageData;
};

/**
 * 自定义 render 函数
 */
export declare type RenderFn = (options: CustomRenderOptions) => void;

/**
 * 用于描述滚动弹幕
 */
export declare class ScrollBarrage extends BaseBarrage {
    readonly barrageType: BarrageType;
    originalLeft: number;
    originalRight: number;
    show: boolean;
    grade: number;
    endTime: number;
    constructor(scrollBarrageOptions: ScrollBarrageOptions, barrageRenderer: BarrageRenderer);
    /**
     * 计算原始的 left 和 right 位置
     */
    calcOriginal(): void;
}

export declare type ScrollBarrageOptions = BaseBarrageOptions & {
    barrageType: 'scroll';
};

export declare type Section = TextSection | ImageSection;

/**
 * 解析完成的片段
 */
export declare type Segment = {
    type: 'text' | 'image';
    value: string;
};

/**
 * 用于描述高级弹幕
 */
export declare class SeniorBarrage extends BaseBarrage {
    readonly barrageType: BarrageType;
    seniorBarrageConfig: SeniorBarrageConfig;
    vx: number;
    vy: number;
    actualStartLocation: Location_2;
    actualEndLocation: Location_2;
    constructor(seniorBarrageOptions: SeniorBarrageOptions, barrageRenderer: BarrageRenderer);
    /**
     * 计算关键点的实际坐标
     */
    calcActualLocation(): void;
    get canvasSize(): {
        width: number;
        height: number;
    };
}

/**
 * 用于描述高级弹幕的运动配置
 */
export declare type SeniorBarrageConfig = {
    startLocation: LocationDefine;
    endLocation: LocationDefine;
    totalDuration: number;
    delay: number;
    motionDuration: number;
};

export declare type SeniorBarrageOptions = BaseBarrageOptions & {
    barrageType: 'senior';
    seniorBarrageConfig: SeniorBarrageConfig;
};

export declare class TextSection {
    readonly sectionType = "text";
    text: string;
    width: number;
    height: number;
    topOffset: number;
    leftOffset: number;
    constructor({ text, width, height, leftOffset, }: TextSectionOptions);
}

/**
 * 描述弹幕内容中的文本片段
 */
export declare type TextSectionOptions = {
    text: string;
    width: number;
    height: number;
    topOffset?: number;
    leftOffset: number;
};

/**
 * 用于描述虚拟轨道，虚拟轨道是实际轨道或者实际轨道的相邻合并
 */
declare class VirtualTrack {
    id: number;
    rtIdArr: number[];
    rtIdSet: Set<number>;
    rtInstanceArr: RealTrack[];
    barrages: ScrollBarrage[];
    constructor(id: number, rtIdArr: number[], rtInstanceArr: RealTrack[]);
    /**
     * 获取轨道中的最后一个滚动弹幕
     */
    getLastBarrage(): ScrollBarrage | undefined;
    /**
     * 向虚拟轨道中添加新的滚动弹幕
     * @param barrage
     */
    push(barrage: ScrollBarrage): void;
    /**
     * 清空所有的弹幕
     */
    clearBarrage(): void;
    /**
     * 获取指定下标的滚动弹幕
     * @param index
     */
    getByIndex(index: number): ScrollBarrage;
    /**
     * 当前虚拟轨道的级别（包含虚拟轨道的数量）
     */
    get grade(): number;
    /**
     * 当前弹幕是不是空的
     */
    get isEmpty(): boolean;
    /**
     * 当前虚拟轨道的 top
     */
    get top(): number;
}

/**
 * 虚拟轨道弹幕布局算法（用于滚动弹幕的计算）
 */
declare class VirtualTrackAlgorithm {
    br: BarrageRenderer;
    bLCalculate: BarrageLayoutCalculate;
    realTracks: RealTrack[];
    virtualTracks: VirtualTrack[];
    realTrackHeight: number;
    realTrackNum: number;
    maxGrade: number;
    vtToVtsMap: Map<VirtualTrack, VirtualTrack[]>;
    gradeToVtsMap: Map<number, VirtualTrack[]>;
    constructor(barrageRenderer: BarrageRenderer, barrageLayoutCalculate: BarrageLayoutCalculate);
    /**
     * 根据 br 的数据初始化实际轨道和虚拟轨道
     * @param realTrackHeight 实际轨道高度
     */
    initTracks(realTrackHeight: number): void;
    /**
     * 对滚动弹幕进行布局计算
     * @param scrollBarrages 滚动弹幕实例数组
     */
    layoutScrollBarrages(scrollBarrages: ScrollBarrage[]): void;
    /**
     * 进行不允许重叠的布局
     * @param scrollBarrages 滚动弹幕实例数组
     */
    avoidOverlapLayout(scrollBarrages: ScrollBarrage[]): void;
    /**
     * 进行允许重叠的布局
     * @param scrollBarrages 滚动弹幕实例数组
     */
    allowOverlapLayout(scrollBarrages: ScrollBarrage[]): void;
    /**
     * 不允许重叠，插入新的弹幕
     * @param scrollBarrage 滚动弹幕实例
     */
    avoidOverlapInsert(scrollBarrage: ScrollBarrage): void;
    /**
     * 发送新的弹幕
     * @param scrollBarrage 滚动弹幕实例
     */
    send(scrollBarrage: ScrollBarrage): void;
    /**
     * 重置存放 实际轨道 和 虚拟轨道 的数组
     */
    resetTracks(): void;
    /**
     * 处理高度变化重新进行布局计算
     * @param scrollBarrages 滚动弹幕实例数组
     */
    heightChangeReLayoutCalc(scrollBarrages: ScrollBarrage[]): void;
    /**
     * 随机一个实际轨道并设置弹幕
     * @param barrage 滚动弹幕实例
     */
    randomTrackBarrage(barrage: ScrollBarrage): void;
    /**
     * 是否允许弹幕相互重叠
     */
    get avoidOverlap(): boolean;
    /**
     * 是否打印关键数据
     */
    get isLogKeyData(): boolean;
    /**
     * 获取一个随机的实际轨道
     */
    getRandomRealTrack(): RealTrack;
    /**
     * 获取滚动弹幕的最小间距
     */
    get minSpace(): number;
}

export { }
