#ifndef _Included_LV2Instance
#define _Included_LV2Instance

void LV2Instance_malloc(LV2Instance **handle, LV2Plugin* plugin, LV2Feature* feature, LV2Config *config);

void LV2Instance_free(LV2Instance **handle);

void LV2Instance_getControlPortValue(LV2Instance *handle, LV2Int32 index, float *value);

void LV2Instance_setControlPortValue(LV2Instance *handle, LV2Int32 index, float value);

void LV2Instance_setMidiMessages(LV2Instance *handle, unsigned char** messages, LV2Int32 length);

void LV2Instance_processAudio(LV2Instance *handle, float** inputs, float** outputs);

#endif