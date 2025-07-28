// 通过 NPM dependencies 成功安装 NPM 包后此处可引入使用
// 如安装 linq 包后就可以引入并使用这个包
// const linq = require("linq");

/**
 * @param {Params}  params     自定义参数
 * @param {Context} context    上下文参数，可通过此参数下钻获取上下文变量信息等
 * @param {Logger}  logger     日志记录器
 *
 * @return 函数的返回数据
 */
module.exports = async function (params, context, logger) {
    logger.info('params', params)
    const {inventoryDetailsList} = params;

    try {
        // 1. 按 ecrId 分组构建 Map<ecrId, Set<materialCode>>
        const ecrMap = new Map();

        for (const detail of inventoryDetailsList) {
            const ecrId = detail.lookup_related_ecr?.id;
            const materialCode = detail.text_related_finished_product_code;

            if (!ecrId || !materialCode) continue;

            if (!ecrMap.has(ecrId)) {
                ecrMap.set(ecrId, new Set());
            }
            ecrMap.get(ecrId).add(materialCode);
        }

        if (ecrMap.size === 0) {
            logger.warn("没有有效的 ecrId + materialCode");
            return {data: inventoryDetailsList};
        }

        // 2. 遍历每个 ecrId 分组发起 OQL 查询
        const statusMap = new Map();

        for (const [ecrId, materialCodeSet] of ecrMap.entries()) {
            const materialCodes = [...materialCodeSet];
            const codeKeys = materialCodes.map((_, idx) => `code_${idx}`);
            const queryParams = {ecrId};

            materialCodes.forEach((code, idx) => {
                queryParams[`code_${idx}`] = code;
            });

            const oqlQuery = `
                SELECT _id, text_material_code, lookup_related_ecr
                FROM object_finished_product_inventory_status
                WHERE lookup_related_ecr._id = $ecrId
                  AND text_material_code IN (${codeKeys.map(k => `$${k}`).join(', ')})
            `;

            logger.info("执行OQL查询", oqlQuery, queryParams);
            const statusRecords = await application.data.oql(oqlQuery, queryParams).execute();

            for (const record of statusRecords) {
                const materialCode = record.text_material_code;
                const ecr = record.lookup_related_ecr?.id || record.lookup_related_ecr?._id;
                const key = `${materialCode}_${ecr}`;
                statusMap.set(key, record._id);
            }
        }

        // 3. 根据结果回填到 detail 中
        const newInventoryDetailsList = inventoryDetailsList.map(detail => {
            const ecrId = detail.lookup_related_ecr?.id;
            const materialCode = detail.text_related_finished_product_code;
            const key = `${materialCode || ''}_${ecrId || ''}`;
            const statusRecordId = statusMap.get(key);

            return {
                ...detail,
                lookup_related_fp_pn: statusRecordId ? {id: statusRecordId} : detail.lookup_related_fp_pn
            };
        });

        logger.info("newInventoryDetailsList", newInventoryDetailsList);
        return {
            data: newInventoryDetailsList
        };

    } catch (error) {
        logger.error('处理过程中发生错误', error);
        throw error;
    }
}
