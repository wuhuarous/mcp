/**
 * @description 根据ECR编号查询是否存在状态为"进行中"或"未开始"的变更对象（优化版）
 */
module.exports = async function (params, context, logger) {
    const {ecrNumber} = params;
    logger.info("params", params);

    if (!ecrNumber) {
        throw new Error("ECR编号不能为空");
    }

    // 状态常量：未开始 & 进行中
    const STATUS_PENDING = "option_98d33542a46";
    const STATUS_IN_PROGRESS = "option_b6913f55076";

    try {
        logger.info(`开始查询ECR编号为 ${ecrNumber} 的变更对象`);

        // 1. 查询ECR记录
        const ecrRecord = await application.data.object("object_ecr_basic_info")
            .select("_id")
            .where({"text_ecr_number": application.operator.eq(ecrNumber)})
            .findOne();

        if (!ecrRecord) {
            logger.info(`未找到ECR编号为 ${ecrNumber} 的记录`);
            return {hasPendingECA: false};
        }

        const ecrId = ecrRecord._id;
        logger.info("ecrId", ecrId);

        // 2. 查询是否存在“进行中/未开始”的变更对象
        const changeObjects = await application.data.object("object_change_object")
            .select("text_object_number") // 不需要 eca_status 字段
            .where({
                "lookup_related_ecr": ecrId
            })
            .find();
        logger.info(`变更对象 ${changeObjects.length} 的记录`, changeObjects);
        const objectNumbers = changeObjects.map(obj => obj.text_object_number);
        // 如果没有结果就提前返回，避免无意义查询
        if (objectNumbers.length === 0) {
            logger.info("没有关联变更对象，跳过后续查询");
            return {hasPendingECA: false};
        }

        const changeObjectsECA = await application.data.object("object_change_object")
            .select("_id")
            .where({
                "text_object_number": application.operator.in(objectNumbers),
                "eca_status": application.operator.in([STATUS_PENDING, STATUS_IN_PROGRESS])
            })
            .limit(1)
            .find();
        logger.info(`变更对象 ${changeObjectsECA.length} 的记录`, changeObjectsECA);

        const hasPending = changeObjectsECA.length > 0;
        logger.info(`是否存在进行中/未开始的变更对象: ${hasPending}`, changeObjectsECA);
        return {hasPendingECA: hasPending};

    } catch (error) {
        logger.error("查询变更对象时出错", error);
        throw new Error("查询变更对象时发生错误");
    }
};
